package com.gracerun.summermq.spring.autoconfigure;


import com.gracerun.summermq.annotation.EnableSummerMQ;
import com.gracerun.summermq.aop.MessageAspect;
import com.gracerun.summermq.event.MessageEventListener;
import com.gracerun.summermq.producer.RedisMessageProducer;
import com.gracerun.summermq.producer.SummerMQTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@Slf4j
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class SummerMQConfiguration implements EnvironmentAware, ImportAware {

    private Environment environment;

    private String producerNamespace;

    private int producerCorePoolSize;

    private int producerMaximumPoolSize;

    private long producerKeepAliveTime;

    private int producerBlockingQueueSize;

    @Bean
    @ConditionalOnMissingBean(RedisMessageProducer.class)
    public RedisMessageProducer redisMessageProducer(StringRedisTemplate stringRedisTemplate) {
        final RedisMessageProducer redisMessageProducer = new RedisMessageProducer(producerNamespace, stringRedisTemplate);
        redisMessageProducer.setCorePoolSize(producerCorePoolSize);
        redisMessageProducer.setMaximumPoolSize(producerMaximumPoolSize);
        redisMessageProducer.setKeepAliveTime(producerKeepAliveTime);
        redisMessageProducer.setBlockingQueueSize(producerBlockingQueueSize);
        return redisMessageProducer;
    }

    @Bean
    public SummerMQTemplate summerMQTemplate() {
        return new SummerMQTemplate();
    }

    @Bean
    public MessageAspect messageAspect() {
        return new MessageAspect();
    }

    @Bean
    @ConditionalOnBean({RedisMessageProducer.class})
    public MessageEventListener messageEventListener() {
        return new MessageEventListener();
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> enableAttrMap = importMetadata.getAnnotationAttributes(EnableSummerMQ.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        this.producerNamespace = this.environment.resolvePlaceholders(enableAttrs.getString("producerNamespace"));
        this.producerCorePoolSize = enableAttrs.getNumber("producerCorePoolSize");
        this.producerMaximumPoolSize = enableAttrs.getNumber("producerMaximumPoolSize");
        this.producerKeepAliveTime = enableAttrs.getNumber("producerKeepAliveTime");
        this.producerBlockingQueueSize = enableAttrs.getNumber("producerBlockingQueueSize");
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
