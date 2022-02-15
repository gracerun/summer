package com.test;

import com.summer.mq.spring.annotation.EnableSummerMq;
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
@EnableSummerMq
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
