package com.gracerun.summermq.annotation;

import com.gracerun.summermq.producer.DataBaseProducer;
import com.gracerun.summermq.producer.RedisMessageProducer;
import com.gracerun.summermq.service.MessagePersistentService;
import com.gracerun.summermq.service.RedisService;
import com.gracerun.summermq.service.impl.DefaultMessagePersistentService;
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
