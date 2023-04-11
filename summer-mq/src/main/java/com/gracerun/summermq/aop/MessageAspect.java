

package com.gracerun.summermq.aop;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.gracerun.log.core.TracerHolder;
import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.constant.MessageStatusConstant;
import com.gracerun.summermq.event.MessageEvent;
import com.gracerun.summermq.producer.RedisMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 消息推送拦截器
 * 触发消息推送事件
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class MessageAspect {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public RedisMessageProducer redisMessageProducer;

    @Around("@annotation(com.gracerun.summermq.annotation.PushMessage)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        final Object[] args = pjp.getArgs();
        if (Objects.nonNull(args)) {
            Arrays.asList(args).forEach(e -> {
                if (Objects.nonNull(e) && e instanceof MessageBody) {
                    applicationEventPublisher.publishEvent(new MessageEvent(initTask((MessageBody) e)));
                }
            });
        }
        return pjp.proceed();
    }

    private MessageBody initTask(MessageBody messageBody) {
        if (Objects.isNull(messageBody.getId())) {
            messageBody.setId(IdWorker.getId());
        }
        if (Objects.isNull(messageBody.getOptimistic())) {
            messageBody.setOptimistic(0);
        }
        Date date = new Date();
        if (Objects.isNull(messageBody.getCreateTime())) {
            messageBody.setCreateTime(date);
        }
        if (Objects.isNull(messageBody.getLastUpdateTime())) {
            messageBody.setLastUpdateTime(date);
        }
        if (Objects.isNull(messageBody.getStatus())) {
            messageBody.setStatus(MessageStatusConstant.INIT);
        }
        if (Objects.isNull(messageBody.getTimes())) {
            messageBody.setTimes(0);
        }
        if (Objects.isNull(messageBody.getNextExecuteTime())) {
            messageBody.setNextExecuteTime(new Date());
        }
        if (Objects.isNull(messageBody.getTimeout())) {
            messageBody.setTimeout(30);
        }
        if (!StringUtils.hasText(messageBody.getNamespace())) {
            messageBody.setNamespace(redisMessageProducer.getProducerNamespace());
        }
        messageBody.setCreateSpanId(TracerHolder.getSanToString());
        return messageBody;
    }

}
