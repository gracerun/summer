package com.gracerun.summermq.annotation;


import com.gracerun.summermq.service.RedisService;
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
public class RedisConfiguration {

    @Bean
    public RedisService redisService() {
        return new RedisService();
    }

    @Bean
    public DefaultRedisScript<Long> resetMessageSizeScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/resetMessageSizeScript.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<List> batchRpopByIndexScript() {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/batchRpopByIndexScript.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<List> batchRpopScript() {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/batchRpopScript.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }
}