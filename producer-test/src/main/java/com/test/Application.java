package com.test;

import com.summer.log.core.TraceRunnableWrapper;
import com.summer.mq.spring.annotation.EnableSummerMq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Tom
 * @version 1.0.0
 * @date 1/17/22
 */
@SpringBootApplication
@EnableScheduling
@EnableSummerMq(producerCorePoolSize = 10, producerMaximumPoolSize = 10)
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        new Thread(new TraceRunnableWrapper(() -> {
            final RuntimeException e = new RuntimeException("异常测试");
            log.error(e.getMessage(), e);
        })).start();
    }
}
