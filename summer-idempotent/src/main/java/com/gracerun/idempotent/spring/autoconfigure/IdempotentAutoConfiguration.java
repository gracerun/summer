package com.gracerun.idempotent.spring.autoconfigure;

import com.gracerun.idempotent.aspect.IdempotentAspect;
import com.gracerun.idempotent.aspect.IdempotentExceptionHandlerAspect;
import com.gracerun.idempotent.repository.IdempotentRepository;
import com.gracerun.idempotent.scheduled.IdempotentScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class IdempotentAutoConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(IdempotentRepository.class)
    public IdempotentAspect idempotentAspect() {
        log.info("AutoInit IdempotentAspect");
        return new IdempotentAspect();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(IdempotentRepository.class)
    public IdempotentExceptionHandlerAspect idempotentExceptionHandlerAspect() {
        log.info("AutoInit IdempotentExceptionHandlerAspect");
        return new IdempotentExceptionHandlerAspect();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(IdempotentRepository.class)
    public IdempotentScheduled idempotentScheduled() {
        log.info("AutoInit IdempotentScheduled");
        return new IdempotentScheduled();
    }
}
