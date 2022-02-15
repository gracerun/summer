package com.summer.log.spring.autoconfigure;

import brave.Tracing;
import com.summer.log.aop.ScheduledLogAspect;
import com.summer.log.core.TracingHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnBean(Tracing.class)
@ConditionalOnClass(ScheduledLogAspect.class)
@AutoConfigureAfter({BraveAutoConfiguration.class})
public class SummerLogAutoConfiguration {

    @Autowired
    private Tracing tracing;

    @Value(("${spring.cloud.client.ipAddress:127.0.0.1}"))
    private String instanceIpAddress;

    @Bean
    public TracingHolder tracerHolder() {
        final TracingHolder tracerHolder = new TracingHolder();
        tracerHolder.setTracing(tracing);
        tracerHolder.setInstanceIpAddress(instanceIpAddress);
        return tracerHolder;
    }

    @Bean
    public ScheduledLogAspect scheduledLogAspect() {
        return new ScheduledLogAspect();
    }

}
