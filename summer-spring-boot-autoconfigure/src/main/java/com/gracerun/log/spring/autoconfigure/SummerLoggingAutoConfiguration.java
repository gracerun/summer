package com.gracerun.log.spring.autoconfigure;

import com.gracerun.log.interceptor.AnnotationLoggingAttributeSource;
import com.gracerun.log.interceptor.LoggingAttributeSource;
import com.gracerun.log.interceptor.LoggingAttributeSourceAdvisor;
import com.gracerun.log.interceptor.LoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SummerLoggingAutoConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LoggingAttributeSourceAdvisor loggingAdvisor(
            LoggingAttributeSource loggingAttributeSource, LoggingInterceptor loggingInterceptor) {
        log.info("Init LoggingAttributeSourceAdvisor");
        LoggingAttributeSourceAdvisor advisor = new LoggingAttributeSourceAdvisor();
        advisor.setLoggingAttributeSource(loggingAttributeSource);
        advisor.setLoggingInterceptor(loggingInterceptor);
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LoggingAttributeSource loggingAttributeSource() {
        log.info("Init LoggingAttributeSource");
        return new AnnotationLoggingAttributeSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LoggingInterceptor loggingInterceptor(LoggingAttributeSource loggingAttributeSource) {
        log.info("Init LoggingInterceptor");
        LoggingInterceptor interceptor = new LoggingInterceptor();
        interceptor.setLoggingAttributeSource(loggingAttributeSource);
        return interceptor;
    }

}
