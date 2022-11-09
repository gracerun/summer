package com.gracerun.summermq.spring.annotation;

import com.gracerun.summermq.constant.QueueConstant;
import com.gracerun.summermq.spring.autoconfigure.SummerMQAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
@Import(SummerMQAutoConfiguration.class)
@Configuration
public @interface EnableSummerMq {

    /**
     * Group name of producer.
     */
    String producerNamespace() default "${spring.profiles.active:" + QueueConstant.DEFAULT_NAMASPACE + "}";

    /**
     * Set ExecutorService params -- corePoolSize
     */
    int producerCorePoolSize() default 4;

    /**
     * Set ExecutorService params -- maximumPoolSize
     */
    int producerMaximumPoolSize() default 4;

    /**
     * Set ExecutorService params -- keepAliveTime
     */
    long producerKeepAliveTime() default 60; //60s

    /**
     * Set ExecutorService params -- blockingQueueSize
     */
    int producerBlockingQueueSize() default 2000;

}
