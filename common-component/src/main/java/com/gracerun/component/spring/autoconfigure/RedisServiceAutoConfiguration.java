package com.gracerun.component.spring.autoconfigure;

import com.gracerun.component.service.RedisService;
import com.gracerun.component.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;


@Slf4j
@AutoConfigureAfter({RedisAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RedisServiceAutoConfiguration {

    @Bean
    public RedisService redisService() {
        log.info("AutoInit RedisService");
        return new RedisService();
    }

    @Bean("commonRedisUtil")
    public RedisUtil redisUtil() {
        log.info("AutoInit RedisUtil");
        return new RedisUtil();
    }
}
