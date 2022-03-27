package com.summer.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.summer.log.core.TraceRunnableWrapper;
import com.summer.mq.bean.DelayRule;
import com.summer.mq.bean.MessageBody;
import com.summer.mq.producer.RedisMessageProducer;
import com.summer.mq.service.ExecutorUtil;
import com.summer.mq.service.QueueNameService;
import com.summer.mq.util.ThreadUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 延时消费
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Slf4j
public class MessageDelayPullConsumer implements InitializingBean, DisposableBean {

    private final ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();

    private final ScheduledTaskRegistrar registrar;

    private final Map<Integer, ThreadPoolExecutor> delayQueueExecutorMap = new HashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisMessageProducer redisMessageProducer;

    @Autowired
    @Qualifier("resetMessageSizeScript")
    private DefaultRedisScript resetMessageSizeScript;

    @Autowired
    @Qualifier("batchRpopByIndexScript")
    private DefaultRedisScript batchRpopByIndexScript;

    @Getter
    @Setter
    private String consumerNamespace;

    public MessageDelayPullConsumer() {
        this.registrar = new ScheduledTaskRegistrar();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final CustomizableThreadFactory scheduledThreadFactory = new CustomizableThreadFactory();
        scheduledThreadFactory.setThreadNamePrefix(ExecutorUtil.fmtThreadNamePrefix(consumerNamespace));
        ScheduledExecutorService localExecutor = Executors.newScheduledThreadPool(DelayRule.DEFAULT_RULE.size(), scheduledThreadFactory);
        registrar.setScheduler(localExecutor);
        addCronTask();
        registrar.afterPropertiesSet();
    }

    @Override
    public void destroy() throws Exception {
        registrar.destroy();
        delayQueueExecutorMap.forEach((k, v) -> ThreadUtils.shutdownGracefully(v, 1000, TimeUnit.MILLISECONDS));
    }

    public void addCronTask() {
        if (DelayRule.DEFAULT_RULE.size() == 0) {
            log.warn("DelayLevel.size() == 0");
            return;
        }
        log.info("initMessageDelayQueue start");
        for (int i = 0; i < DelayRule.DEFAULT_RULE.size(); i++) {
            String threadNamePrefix = ExecutorUtil.fmtThreadNamePrefix(consumerNamespace, i);
            ThreadPoolExecutor threadPoolExecutor;
            if (i <= 5) {
                threadPoolExecutor = ExecutorUtil.createExecutor(threadNamePrefix, 2, 2, 0, 2000, callerRunsPolicy);
            } else {
                threadPoolExecutor = ExecutorUtil.createExecutor(threadNamePrefix, 1, 1, 0, 2000, callerRunsPolicy);
            }
            delayQueueExecutorMap.put(i, threadPoolExecutor);
            registrar.addCronTask(new TraceRunnableWrapper(new SyncConsumer(consumerNamespace, i, threadPoolExecutor)), DelayRule.DEFAULT_RULE.getCronExpression(i));
        }
        log.info("initMessageDelayQueue end");

    }

    class SyncConsumer implements Runnable {

        private String queueName;

        private String indexKey;

        private ThreadPoolExecutor threadPoolExecutor;

        public SyncConsumer(String consumerGroup, Integer delayLevel, ThreadPoolExecutor threadPoolExecutor) {
            this.threadPoolExecutor = threadPoolExecutor;
            this.queueName = QueueNameService.fmtDelayQueueName(consumerGroup, delayLevel);
            this.indexKey = QueueNameService.fmtDelayIndexQueueName(consumerGroup, delayLevel);
        }

        @Override
        public void run() {
            long messageSize = 0;
            try {
                final Object size = stringRedisTemplate.execute(resetMessageSizeScript, Arrays.asList(indexKey, queueName));
                if (Objects.nonNull(size)) {
                    messageSize = (Long) size;
                }
            } catch (Exception e) {
                log.error("queueName:{}, getSize error:{}", queueName, e.getMessage());
                log.error(e.getMessage(), e);
                return;
            }

            if (messageSize <= 0) {
                return;
            }

            log.debug("{} begin messageSize:{}", queueName, messageSize);
            for (long increment = messageSize; increment > 0; ) {
                try {
                    final Object results = stringRedisTemplate.execute(batchRpopByIndexScript, Arrays.asList(indexKey, queueName), "10");
                    if (Objects.nonNull(results) && results instanceof List && ((List) results).size() > 0) {
                        increment = (Long) ((List) results).get(0);
                        List<String> range = (List) ((List) results).get(1);
                        if (!CollectionUtils.isEmpty(range)) {
                            List<MessageBody> messageList = new ArrayList<>(range.size());
                            for (String v : range) {
                                messageList.add(JSON.parseObject(v, MessageBody.class));
                            }
                            submit(messageList);
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    log.error("queueName:{}, batchRpop error:{}", queueName, e.getMessage());
                    log.error(e.getMessage(), e);
                }
            }
        }

        public void submit(List<MessageBody> list) {
            if (Objects.nonNull(threadPoolExecutor)) {
                threadPoolExecutor.submit(new TraceRunnableWrapper(() -> redisMessageProducer.syncSend(queueName, list)));
            } else {
                redisMessageProducer.syncSend(queueName, list);
            }
        }
    }

}


