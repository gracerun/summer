package com.summer.log.spring.autoconfigure;

import com.summer.log.aop.LogSchedulingAspect;
import com.summer.log.core.TracerHolder;
import com.summer.log.interceptor.AnnotationLoggingAttributeSource;
import com.summer.log.interceptor.LoggingAttributeSource;
import com.summer.log.interceptor.LoggingAttributeSourceAdvisor;
import com.summer.log.interceptor.LoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Slf4j
@Configuration
@AutoConfigureAfter({BraveAutoConfiguration.class})
public class SummerLogAutoConfiguration {

    @Autowired
    private Tracer tracer;

    @Value(("${spring.cloud.client.ipAddress:127.0.0.1}"))
    private String instanceIpAddress;

    @Bean
    public TracerHolder tracerHolder() {
        log.info("Init TracerHolder");
        final TracerHolder tracerHolder = new TracerHolder();
        tracerHolder.setTracer(tracer);
        tracerHolder.setInstanceIpAddress(instanceIpAddress);
        return tracerHolder;
    }

    @Bean
    public LogSchedulingAspect logSchedulingAspect() {
        log.info("Init LogSchedulingAspect");
        return new LogSchedulingAspect();
    }

    @Bean
    public LoggingAttributeSourceAdvisor loggingAdvisor(
            LoggingAttributeSource loggingAttributeSource, LoggingInterceptor loggingInterceptor) {
        LoggingAttributeSourceAdvisor advisor = new LoggingAttributeSourceAdvisor();
        advisor.setLoggingAttributeSource(loggingAttributeSource);
        advisor.setLoggingInterceptor(loggingInterceptor);
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return advisor;
    }

    @Bean
    public LoggingAttributeSource loggingAttributeSource() {
        return new AnnotationLoggingAttributeSource();
    }

    @Bean
    public LoggingInterceptor loggingInterceptor(LoggingAttributeSource loggingAttributeSource) {
        LoggingInterceptor interceptor = new LoggingInterceptor();
        interceptor.setLoggingAttributeSource(loggingAttributeSource);
        return interceptor;
    }

}
