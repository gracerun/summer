package com.test;

import com.summer.mq.spring.annotation.EnableSummerMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Tom
 * @version 1.0.0
 * @date 1/17/22
 */
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
@EnableSummerMq(producerCorePoolSize = 10, producerMaximumPoolSize = 10)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
