package com.gracerun.idempotent.spring.autoconfigure;

import com.gracerun.idempotent.aspect.IdempotentAspect;
import com.gracerun.idempotent.aspect.IdempotentExceptionHandlerAspect;
import com.gracerun.idempotent.repository.IdempotentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({IdempotentAspect.class, IdempotentExceptionHandlerAspect.class})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class IdempotentAutoConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(IdempotentRepository.class)
    public IdempotentAspect idempotentAspect() {
        log.info("Init IdempotentAspect");
        return new IdempotentAspect();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(IdempotentRepository.class)
    public IdempotentExceptionHandlerAspect idempotentExceptionHandlerAspect() {
        log.info("Init IdempotentExceptionHandlerAspect");
        return new IdempotentExceptionHandlerAspect();
    }
}
