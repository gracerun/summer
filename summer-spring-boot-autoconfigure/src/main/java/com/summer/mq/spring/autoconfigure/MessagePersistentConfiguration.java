package com.summer.mq.spring.autoconfigure;

import com.summer.mq.producer.DataBaseProducer;
import com.summer.mq.producer.RedisMessageProducer;
import com.summer.mq.service.MessagePersistentService;
import com.summer.mq.service.RedisService;
import com.summer.mq.service.impl.DefaultMessagePersistentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@ConditionalOnClass({RedisMessageProducer.class, JdbcTemplate.class, RedisService.class})
public class MessagePersistentConfiguration {

    @Bean
    @ConditionalOnMissingBean(MessagePersistentService.class)
    public DefaultMessagePersistentService defaultMessagePersistentService() {
        return new DefaultMessagePersistentService();
    }

    @Bean
    @ConditionalOnMissingBean(DataBaseProducer.class)
    public DataBaseProducer dataBaseProducer() {
        final DataBaseProducer dataBaseProducer = new DataBaseProducer();
        return dataBaseProducer;
    }

}
