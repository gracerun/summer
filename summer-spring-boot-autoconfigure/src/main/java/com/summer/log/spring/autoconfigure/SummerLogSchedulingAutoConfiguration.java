package com.summer.log.spring.autoconfigure;

import com.summer.log.aop.LogSchedulingAspect;
import com.summer.log.core.TracerHolder;
import com.summer.log.filter.HttpTraceFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({BraveAutoConfiguration.class})
public class SummerLogSchedulingAutoConfiguration {

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
    @ConditionalOnProperty(name = "request.trace.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean httpTraceFilter() {
        log.info("Init HttpTraceFilter");
        FilterRegistrationBean bean = new FilterRegistrationBean();
        HttpTraceFilter httpTraceFilter = new HttpTraceFilter();
        bean.setFilter(httpTraceFilter);
        bean.setName(httpTraceFilter.getClass().getSimpleName());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
