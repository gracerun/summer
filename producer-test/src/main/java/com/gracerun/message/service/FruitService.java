package com.gracerun.message.service;

import com.gracerun.log.annotation.Logging;
import com.gracerun.log.annotation.ThrowableLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Logging(throwableLog = {@ThrowableLog(throwable = Throwable.class, maxRow = 5)})
public class FruitService {

    @Logging(throwableLog = {@ThrowableLog(throwable = Throwable.class, maxRow = 2)})
    public void printName() {
        log.info("fruitInner:{}", System.currentTimeMillis());
    }
}
