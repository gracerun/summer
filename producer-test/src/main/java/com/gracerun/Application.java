package com.gracerun;

import com.gracerun.summermq.annotation.EnableSummerMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
//@EnableDiscoveryClient
//@EnableFeignClients
@EnableSummerMQ(producerCorePoolSize = 10, producerMaximumPoolSize = 10)
@Slf4j
public class Application implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("系统启动成功\n.....\nsuccess");
    }
}
