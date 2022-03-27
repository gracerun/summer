package com.summer.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.summer.log.core.TraceRunnableWrapper;
import com.summer.mq.annotation.SummerMQMessageListener;
import com.summer.mq.bean.DelayRule;
import com.summer.mq.bean.MessageBody;
import com.summer.mq.constant.QueueConstant;
import com.summer.mq.constant.ServiceState;
import com.summer.mq.exception.MQClientException;
import com.summer.mq.factory.MQClientInstance;
import com.summer.mq.producer.RedisMessageProducer;
import com.summer.mq.service.ExecutorUtil;
import com.summer.mq.service.MessagePersistentService;
import com.summer.mq.service.QueueNameService;
import com.summer.mq.util.ThreadUtils;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 拉取消息
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Slf4j
public class RedisMessagePullConsumer<M extends MessageListener> implements DisposableBean {

    /**
     * Delay some time when exception occur
     */
    private static final long PULL_TIME_DELAY_MILLS_WHEN_EXCEPTION = 3000;
    /**
     * Flow control interval
     */
    private static final long PULL_TIME_DELAY_MILLS_WHEN_FLOW_CONTROL = 50;

    /**
     * 拉取消息线程池
     */
    private ThreadPoolExecutor pullThread;

    /**
     * message消费线程池
     */
    private ThreadPoolExecutor consumerThread;

    @Getter
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private RedisMessageProducer redisMessageProducer;

    @Autowired
    @Qualifier("batchRpopScript")
    private DefaultRedisScript batchRpopScript;

    @Getter
    @Setter
    private SummerMQMessageListener summerMQMessageListener;

    @Getter
    @Setter
    private DelayRule delayRule = DelayRule.DEFAULT_RULE;

    @Getter
    @Autowired(required = false)
    private MessagePersistentService messagePersistentService;

    private String queueName;

    @Getter
    @Setter
    private String consumerNamespace;

    @Getter
    @Setter
    private String topic;

    private int blockingTimeout = 3;

    @Getter
    @Setter
    private int batchRpopSize = 10;

    @Getter
    @Setter
    private int corePoolSize = 4;

    @Getter
    @Setter
    private int maximumPoolSize = 4;

    @Getter
    @Setter
    private int keepAliveTime = 60;

    @Getter
    @Setter
    private int blockingQueueSize = 2000;

    @Getter
    @Setter
    protected M messageListener;

    private volatile ServiceState serviceState = ServiceState.CREATE_JUST;

    private MQClientInstance mqClientInstance;

    private long queueFlowControlTimes = 0;

    public RedisMessagePullConsumer(String topic) {
        this.topic = topic;
    }

    public synchronized void start() throws MQClientException {
        switch (this.serviceState) {
            case CREATE_JUST:
                queueName = QueueNameService.fmtTopicQueueName(consumerNamespace, topic);

                if (StringUtils.hasText(summerMQMessageListener.delayExpression())
                        && !QueueConstant.DELAY_EXPRESSION.equals(summerMQMessageListener.delayExpression())) {
                    delayRule = new DelayRule(summerMQMessageListener.delayExpression());
                }
                delayRule.setRepeatRetry(summerMQMessageListener.repeatRetry());

                log.info("the consumer [{}] start beginning", queueName);

                final Duration timeout = redisProperties.getTimeout();
                final long seconds = timeout.getSeconds();
                if (seconds <= 2) {
                    blockingTimeout = 1;
                } else {
                    blockingTimeout = (int) seconds - 1;
                }

                this.serviceState = ServiceState.START_FAILED;

                pullThread = ExecutorUtil.createExecutor(queueName + "-pull-", 1, 1, 0, 1, new ThreadPoolExecutor.CallerRunsPolicy());
                consumerThread = ExecutorUtil.createExecutor(queueName + "-consumer-", corePoolSize, maximumPoolSize, keepAliveTime, blockingQueueSize, new ThreadPoolExecutor.CallerRunsPolicy());

                mqClientInstance = MQClientInstance.getInstance();

                boolean registerOK = mqClientInstance.registerConsumer(queueName, this);
                if (!registerOK) {
                    this.serviceState = ServiceState.CREATE_JUST;
                    throw new MQClientException("The consumer queue [" + queueName + "] has been created before, specify another name please.", null);
                }

                mqClientInstance.start();
                executePullRequestImmediately(new PullRequest(consumerNamespace, queueName).setBatchSize(batchRpopSize));
                log.info("the consumer [{}] start OK.", queueName);
                this.serviceState = ServiceState.RUNNING;
                break;
            case RUNNING:
            case START_FAILED:
            case SHUTDOWN_ALREADY:
                throw new MQClientException("The PushConsumer service state not OK, maybe started once, " + this.serviceState, null);
            default:
                break;
        }

    }

    @Override
    public void destroy() throws Exception {
        ThreadUtils.shutdownGracefully(this.pullThread, 1000, TimeUnit.MILLISECONDS);
        ThreadUtils.shutdownGracefully(this.consumerThread, 1000, TimeUnit.MILLISECONDS);
    }

    public void pullMessage(PullRequest pullRequest) {
        int cachedMessageCount = consumerThread.getQueue().size();
        if (cachedMessageCount > blockingQueueSize) {
            this.executePullRequestLater(pullRequest, PULL_TIME_DELAY_MILLS_WHEN_FLOW_CONTROL);
            if ((queueFlowControlTimes++ % 1000) == 0) {
                log.warn("the cached message count exceeds the threshold {}, so do flow control, count={}, pullRequest={}, flowControlTimes={}",
                        blockingQueueSize, cachedMessageCount, pullRequest, queueFlowControlTimes);
            }
            return;
        }
        pullThread.execute(new TraceRunnableWrapper(new PullMessage(pullRequest)));
    }

    private void executePullRequestLater(final PullRequest pullRequest, final long timeDelay) {
        this.mqClientInstance.getPullMessageService().executePullRequestLater(pullRequest, timeDelay);
    }

    public void executePullRequestImmediately(final PullRequest pullRequest) {
        this.mqClientInstance.getPullMessageService().executePullRequestImmediately(pullRequest);
    }

    class PullMessage implements Runnable {

        private PullRequest pullRequest;

        public PullMessage(PullRequest pullRequest) {
            this.pullRequest = pullRequest;
        }

        @Override
        public void run() {
            if (!pullRequest.isBatchPull()) {
                try {
                    String s = stringRedisTemplate.opsForList().rightPop(queueName, blockingTimeout, TimeUnit.SECONDS);
                    if (StringUtils.hasText(s)) {
                        pullRequest.setBatchPull(true);
                        submit(s);
                    }
                    RedisMessagePullConsumer.this.executePullRequestImmediately(pullRequest);
                } catch (QueryTimeoutException | RedisCommandTimeoutException e) {
                    RedisMessagePullConsumer.this.executePullRequestImmediately(pullRequest);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    executePullRequestLater(pullRequest, PULL_TIME_DELAY_MILLS_WHEN_EXCEPTION);
                    return;
                }
            } else {
                try {
                    long startTime = System.currentTimeMillis();
                    final Object results = stringRedisTemplate.execute(batchRpopScript, Arrays.asList(queueName), pullRequest.getBatchSize() + "");
                    if (Objects.nonNull(results) && results instanceof List && ((List) results).size() > 0) {
                        List<String> range = (List) results;
                        for (String v : range) {
                            submit(v, startTime);
                        }
                    } else {
                        pullRequest.setBatchPull(false);
                    }
                    RedisMessagePullConsumer.this.executePullRequestImmediately(pullRequest);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    executePullRequestLater(pullRequest, PULL_TIME_DELAY_MILLS_WHEN_EXCEPTION);
                    return;
                }
            }

        }

        private void submit(String s) {
            submit(s, -1);
        }

        private void submit(String s, long startTime) {
            final MessageBody messageBody = JSON.parseObject(s, MessageBody.class);
            final long endTime = System.currentTimeMillis();
            if (startTime <= 0) {
                log.debug("queueName:{}, msgId:{}, pullDelay:{}ms", queueName, messageBody.getId(), (endTime - messageBody.getNextExecuteTime().getTime()));
            } else {
                log.debug("queueName:{}, msgId:{}, pullTime:{}ms, pullDelay:{}ms", queueName, messageBody.getId(), (endTime - startTime), (endTime - messageBody.getNextExecuteTime().getTime()));
            }

            consumerThread.submit(new TraceRunnableWrapper(new MessageConsumer(RedisMessagePullConsumer.this, redisMessageProducer, pullRequest, messageBody)));
        }

    }

}


