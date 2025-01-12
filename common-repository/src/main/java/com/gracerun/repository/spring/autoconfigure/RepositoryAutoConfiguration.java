package com.gracerun.repository.spring.autoconfigure;

import com.gracerun.repository.config.IdWorkConfigInit;
import com.gracerun.repository.service.AutoIncrementService;
import com.gracerun.repository.service.SequenceMetric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RepositoryAutoConfiguration {

    @Bean
    public AutoIncrementService autoIncrementService() {
        log.info("AutoInit AutoIncrementService");
        return new AutoIncrementService();
    }

    @Bean
    @ConditionalOnClass(InetUtils.class)
    public SequenceMetric sequenceMetric(InetUtils inetUtils) {
        return new SequenceMetric(inetUtils);
    }

    @Bean
    public IdWorkConfigInit idWorkConfigInit() {
        return new IdWorkConfigInit();
    }

}
