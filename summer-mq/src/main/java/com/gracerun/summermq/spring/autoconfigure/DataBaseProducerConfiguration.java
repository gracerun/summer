package com.gracerun.summermq.spring.autoconfigure;

import com.gracerun.summermq.producer.DataBaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
public class DataBaseProducerConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataBaseProducer.class)
    public DataBaseProducer dataBaseProducer() {
        log.info("AutoInit DataBaseProducer");
        return new DataBaseProducer();
    }

}
