package com.summer.log.spring.autoconfigure;

import com.summer.log.aop.ScheduledLogAspect;
import com.summer.log.core.TracerHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ScheduledLogAspect scheduledLogAspect() {
        log.info("Init ScheduledLogAspect");
        return new ScheduledLogAspect();
    }

}
