package com.summer.mq.annotation;

import com.summer.mq.constant.QueueConstant;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SummerMQMessageListener {

    String topic();

    String consumerNamespace() default QueueConstant.DEFAULT_NAMASPACE;

    String delayExpression() default QueueConstant.DELAY_EXPRESSION;

    boolean repeatRetry() default false;

    int batchRpopSize() default 10;

    /**
     * Set ExecutorService params -- corePoolSize
     */
    int corePoolSize() default 4;

    /**
     * Set ExecutorService params -- maximumPoolSize
     */
    int maximumPoolSize() default 4;

    /**
     * Set ExecutorService params -- keepAliveTime
     */
    int keepAliveTime() default 60; //60s

    /**
     * Set ExecutorService params -- blockingQueueSize
     */
    int blockingQueueSize() default 2000;

}
