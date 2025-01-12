package com.gracerun.log.spring.autoconfigure;

import com.gracerun.log.aop.LogSchedulingAspect;
import com.gracerun.log.core.TracerHolder;
import com.gracerun.log.filter.HttpTraceFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.cloud.sleuth.brave.bridge.BraveTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

@Slf4j
@AutoConfigureAfter({BraveAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SummerLogSchedulingAutoConfiguration {

    @Resource
    private BraveTracer braveTracer;

    @Value(("${spring.cloud.client.ipAddress:127.0.0.1}"))
    private String instanceIpAddress;

    @Bean
    public TracerHolder tracerHolder() {
        log.info("AutoInit TracerHolder");
        final TracerHolder tracerHolder = new TracerHolder();
        tracerHolder.setBraveTracer(braveTracer);
        tracerHolder.setInstanceIpAddress(instanceIpAddress);
        return tracerHolder;
    }

    @Bean
    public LogSchedulingAspect logSchedulingAspect() {
        log.info("AutoInit LogSchedulingAspect");
        return new LogSchedulingAspect();
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "request.trace.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<HttpTraceFilter> httpTraceFilter() {
        log.info("AutoInit HttpTraceFilter");
        FilterRegistrationBean<HttpTraceFilter> bean = new FilterRegistrationBean<>();
        HttpTraceFilter httpTraceFilter = new HttpTraceFilter();
        bean.setFilter(httpTraceFilter);
        bean.setName(httpTraceFilter.getClass().getSimpleName());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
