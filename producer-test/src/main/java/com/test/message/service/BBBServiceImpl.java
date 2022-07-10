package com.test.message.service;

import com.summer.log.annotation.Logging;
import com.summer.log.annotation.ThrowableLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Logging(throwableLog = {@ThrowableLog(throwable = RuntimeException.class, maxRow = 0)})
public class BBBServiceImpl {

    public String printBBB() {
        try {
            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 3));
        } catch (InterruptedException e) {

        }
        log.info("-------printBBB:{}", System.currentTimeMillis());
        throw new RuntimeException("printBBB失败");
    }
}
