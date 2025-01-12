package com.gracerun.summermq.spring.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * mq配置
 *
 * @author Tom
 * @version 1.0.0
 * @date 1/10/22
 */
@Slf4j
public class RedisScriptConfiguration {

    @Bean
    public DefaultRedisScript<Long> resetMessageSizeScript() {
        log.info("AutoInit resetMessageSizeScript");
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/resetMessageSizeScript.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<List> batchRpopByIndexScript() {
        log.info("AutoInit batchRpopByIndexScript");
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/batchRpopByIndexScript.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<List> batchRpopScript() {
        log.info("AutoInit batchRpopScript");
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/batchRpopScript.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }
}