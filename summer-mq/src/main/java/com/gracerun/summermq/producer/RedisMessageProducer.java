package com.gracerun.summermq.producer;

import com.alibaba.fastjson.JSON;
import com.gracerun.log.core.TraceRunnableWrapper;
import com.gracerun.summermq.bean.DelayRule;
import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.constant.ServiceState;
import com.gracerun.summermq.exception.MQClientException;
import com.gracerun.summermq.factory.MQClientInstance;
import com.gracerun.summermq.service.ExecutorUtil;
import com.gracerun.summermq.service.QueueNameService;
import com.gracerun.summermq.util.ThreadUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisPipelineException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息生产者
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Slf4j
public class RedisMessageProducer implements InitializingBean, DisposableBean {

    private ThreadPoolExecutor producerThread;

    @Getter
    @Setter
    private String producerNamespace;

    @Getter
    @Setter
    private int corePoolSize = 4;

    @Getter
    @Setter
    private int maximumPoolSize = 4;

    @Getter
    @Setter
    private long keepAliveTime = 60;

    @Getter
    @Setter
    private int blockingQueueSize = 2000;

    private StringRedisTemplate stringRedisTemplate;

    private MQClientInstance mqClientInstance;

    private ServiceState serviceState = ServiceState.CREATE_JUST;

    public RedisMessageProducer(String producerNamespace, StringRedisTemplate redisTemplate) {
        this.producerNamespace = producerNamespace;
        this.stringRedisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start(true);
    }

    public void start(final boolean startFactory) throws MQClientException {
        switch (this.serviceState) {
            case CREATE_JUST:
                log.info("the producer start beginning");
                this.serviceState = ServiceState.START_FAILED;

                producerThread = ExecutorUtil.createExecutor("summer-producer-", corePoolSize, maximumPoolSize, keepAliveTime, blockingQueueSize, new ThreadPoolExecutor.CallerRunsPolicy());

                this.mqClientInstance = MQClientInstance.getInstance();

                boolean registerOK = mqClientInstance.registerProducer(producerNamespace, this);
                if (!registerOK) {
                    this.serviceState = ServiceState.CREATE_JUST;
                    throw new MQClientException("The producer namespace[" + producerNamespace + "] has been created before, specify another name please.", null);
                }
                if (startFactory) {
                    mqClientInstance.start();
                }
                this.serviceState = ServiceState.RUNNING;
                log.info("the producer start OK.");
                break;
            case RUNNING:
            case START_FAILED:
            case SHUTDOWN_ALREADY:
                throw new MQClientException("The producer service state not OK, maybe started once, "
                        + this.serviceState, null);
            default:
                break;
        }
    }

    @Override
    public void destroy() throws Exception {
        ThreadUtils.shutdownGracefully(this.producerThread, 1000, TimeUnit.MILLISECONDS);
    }

    public void asyncSend(List<MessageBody> list) {
        if (!CollectionUtils.isEmpty(list)) {
            producerThread.submit(new TraceRunnableWrapper(() -> RedisMessageProducer.this.syncSend("", list), true));
        }
    }

    public void asyncSend(MessageBody messageBody) {
        asyncSend("", messageBody);
    }

    public void asyncSend(String beforeQueueName, MessageBody messageBody) {
        if (Objects.nonNull(messageBody)) {
            producerThread.submit(new TraceRunnableWrapper(() -> RedisMessageProducer.this.syncSend(beforeQueueName, messageBody), true));
        }
    }

    public void syncSend(String beforeQueueName, MessageBody messageBody) {
        if (Objects.nonNull(messageBody)) {
            syncSend(beforeQueueName, Arrays.asList(messageBody));
        }
    }

    public void syncSend(String beforeQueueName, List<MessageBody> list) {
        try {
            stringRedisTemplate.executePipelined((RedisCallback<?>) (connection) -> {
                list.forEach(m -> {
                    if (!StringUtils.hasText(m.getNamespace())) {
                        m.setNamespace(producerNamespace);
                    }
                    final String queueName = nextQueueName(m);
                    final String body = JSON.toJSONString(m);
                    try {
                        connection.lPush(stringRedisTemplate.getStringSerializer().serialize(queueName)
                                , stringRedisTemplate.getStringSerializer().serialize(body));
                        log.debug("send success msgId:{}, {}-->{}", m.getId(), beforeQueueName, queueName);
                    } catch (Exception e) {
                        log.error("send fail msgId:{}, {}-->{}", m.getId(), beforeQueueName, queueName, e);
                    }
                });
                return null;
            });
        } catch (RedisPipelineException | RedisConnectionFailureException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String nextQueueName(MessageBody messageBody) {
        int seconds = (int) ((messageBody.getNextExecuteTime().getTime() - System.currentTimeMillis()) / 1000);
        String queueName = QueueNameService.fmtTopicQueueName(messageBody.getNamespace(), messageBody.getBusinessType());
        if (seconds <= 0) {
            return queueName;
        } else if (seconds == 1) {
            return QueueNameService.fmtDelayQueueName(messageBody.getNamespace(), 0);
        }

        final int secondOfDay = new DateTime().getSecondOfDay();
        int nearestTime = seconds;
        int finalLevel = -1;

        if (seconds <= 120) {
            for (int i = 0; i <= 6; i++) {
                final int nextSeconds = DelayRule.DEFAULT_RULE.getSeconds(i) - secondOfDay % DelayRule.DEFAULT_RULE.getSeconds(i);
                if (nextSeconds == seconds) {
                    finalLevel = i;
                    break;
                }
                int n = seconds - nextSeconds;
                if (n < nearestTime && nextSeconds < seconds) {
                    nearestTime = n;
                    finalLevel = i;
                }
            }
        }

        if (finalLevel >= 0) {
            return QueueNameService.fmtDelayQueueName(messageBody.getNamespace(), finalLevel);
        }

        for (int i = DelayRule.DEFAULT_RULE.size() - 1; i >= 0; i--) {
            if (seconds >= DelayRule.DEFAULT_RULE.getSeconds(i)) {
                queueName = QueueNameService.fmtDelayQueueName(messageBody.getNamespace(), i);
                break;
            }
        }

        return queueName;
    }

}


