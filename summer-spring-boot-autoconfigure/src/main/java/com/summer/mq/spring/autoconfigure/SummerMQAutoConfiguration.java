package com.summer.mq.spring.autoconfigure;

import com.summer.log.spring.autoconfigure.SummerLogAutoConfiguration;
import com.summer.mq.aop.MessageAspect;
import com.summer.mq.config.MqConfiguration;
import com.summer.mq.event.MessageEventListener;
import com.summer.mq.producer.RedisMessageProducer;
import com.summer.mq.producer.SummerMQTemplate;
import com.summer.mq.spring.annotation.EnableSummerMq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@Slf4j
@Configuration
@Import({MqConfiguration.class, MessagePersistentConfiguration.class, ListenerContainerConfiguration.class, DelayConsumerConfiguration.class})
@AutoConfigureAfter({RedisAutoConfiguration.class, SummerLogAutoConfiguration.class})
public class SummerMQAutoConfiguration implements ImportAware {

    @Autowired
    private StandardEnvironment environment;

    private String producerNamespace;

    private int producerCorePoolSize;

    private int producerMaximumPoolSize;

    private long producerKeepAliveTime;

    private int producerBlockingQueueSize;

    @Bean
    @ConditionalOnMissingBean(RedisMessageProducer.class)
    public RedisMessageProducer defaultMQProducer(StringRedisTemplate stringRedisTemplate) {
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
        Map<String, Object> enableAttrMap = importMetadata.getAnnotationAttributes(EnableSummerMq.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        this.producerNamespace = this.environment.resolvePlaceholders(enableAttrs.getString("producerNamespace"));
        this.producerCorePoolSize = enableAttrs.getNumber("producerCorePoolSize");
        this.producerMaximumPoolSize = enableAttrs.getNumber("producerMaximumPoolSize");
        this.producerKeepAliveTime = enableAttrs.getNumber("producerKeepAliveTime");
        this.producerBlockingQueueSize = enableAttrs.getNumber("producerBlockingQueueSize");
    }
}
