package com.gracerun.summermq.spring.autoconfigure;

import com.gracerun.summermq.service.MessagePersistentService;
import com.gracerun.summermq.service.impl.DefaultMessagePersistentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
public class MessagePersistentConfiguration {

    @Bean
    @ConditionalOnMissingBean(MessagePersistentService.class)
    public DefaultMessagePersistentService defaultMessagePersistentService() {
        log.info("AutoInit DefaultMessagePersistentService");
        return new DefaultMessagePersistentService();
    }

}
