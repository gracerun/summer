package com.summer.mq.consumer;

import com.summer.log.core.IpUtil;
import com.summer.log.core.TracingHolder;
import com.summer.mq.bean.DelayRule;
import com.summer.mq.bean.MessageBody;
import com.summer.mq.constant.ConsumeStatus;
import com.summer.mq.constant.MessageStatusConstant;
import com.summer.mq.producer.RedisMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Objects;

@Slf4j
public class MessageConsumer implements Runnable {

    private RedisMessagePullConsumer redisMessagePullConsumer;

    private RedisMessageProducer redisMessageProducer;

    private DelayRule delayRule;

    private PullRequest pullRequest;

    private MessageBody message;

    public MessageConsumer(RedisMessagePullConsumer redisMessagePullConsumer,
                           RedisMessageProducer redisMessageProducer,
                           PullRequest pullRequest,
                           MessageBody message) {
        this.redisMessagePullConsumer = redisMessagePullConsumer;
        this.delayRule = redisMessagePullConsumer.getDelayRule();
        this.redisMessageProducer = redisMessageProducer;
        this.pullRequest = pullRequest;
        this.message = message;
    }

    @Override
    public void run() {
        ConsumeStatus consumeStatus = ConsumeStatus.RECONSUME_LATER;
        final Date oldNextExecuteTime = message.getNextExecuteTime();
        long startTime = System.currentTimeMillis();
        long consumeTime;
        try {
            log.debug("queueName:{}, msgId:{}, consumerDelay:{}ms", pullRequest.getMessageQueueName(), message.getId(), (startTime - oldNextExecuteTime.getTime()));
            consumeStatus = redisMessagePullConsumer.messageListener.consumeMessage(message);
            consumeTime = System.currentTimeMillis() - startTime;
            log.debug("queueName:{}, msgId:{}, consumeStatus:{}, consumeTime:{}ms", pullRequest.getMessageQueueName(), message.getId(), consumeStatus, consumeTime);
        } catch (Exception e) {
            consumeTime = System.currentTimeMillis() - startTime;
            log.error("queueName:{}, msgId:{}, error:{}", pullRequest.getMessageQueueName(), message.getId(), e.getMessage());
            log.error(e.getMessage(), e);
        }

        try {
            int size = updateMessage(message, consumeStatus, (int) consumeTime, oldNextExecuteTime);
            if (size != 1) {
                log.warn("Message Concurrent update fail msgId:{}, size:{}", message.getId(), size);
                return;
            }
        } catch (Exception e) {
            log.error("updateMessage msgId:{}, error:{}", message.getId(), e.getMessage());
            log.error(e.getMessage(), e);
        }

        if (ConsumeStatus.RECONSUME_LATER == consumeStatus) {
            if (!MessageStatusConstant.SUCCESS.equals(message.getStatus())
                    && !MessageStatusConstant.FAIL.equals(message.getStatus())) {
                redisMessageProducer.asyncSend(pullRequest.getMessageQueueName(), message);
            }
        }
    }

    private int updateMessage(MessageBody message, ConsumeStatus consumeStatus, Integer time, Date oldNextExecuteTime) {
        if (message.getTimes() != null) {
            message.setTimes(message.getTimes() + 1);
        } else {
            message.setTimes(1);
        }

        if (ConsumeStatus.CONSUME_SUCCESS == consumeStatus) {
            message.setStatus(MessageStatusConstant.SUCCESS);
        } else if (ConsumeStatus.RECONSUME_LATER == consumeStatus) {
            if ((message.getTimes() - 1) >= delayRule.size() && !delayRule.isRepeatRetry()) {
                message.setStatus(MessageStatusConstant.FAIL);
            } else {
                if (oldNextExecuteTime == message.getNextExecuteTime() && oldNextExecuteTime.getTime() == message.getNextExecuteTime().getTime()) {
                    DateTime dateTime = new DateTime().plusSeconds(delayRule.getSeconds((message.getTimes() - 1) % delayRule.size()));
                    message.setNextExecuteTime(dateTime.toDate());
                }
            }
        }

        message.setIp(IpUtil.getIp());
        message.setTime(time);
        message.setLastUpdateSpanId(TracingHolder.getSanToString());
        message.setLastUpdateTime(new Date());

        if (Objects.nonNull(redisMessagePullConsumer.getMessagePersistentService()) && Boolean.TRUE.equals(message.getPersistent())) {
            int size = redisMessagePullConsumer.getMessagePersistentService().updateById(message);
            if (size == 1) {
                message.setOptimistic(message.getOptimistic() + 1);
            }
            return size;
        } else {
            return 1;
        }
    }

}