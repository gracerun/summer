package com.gracerun.message.service;

import com.gracerun.log.annotation.Logging;
import com.gracerun.log.annotation.ThrowableLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Logging(throwableLog = {@ThrowableLog(throwable = RuntimeException.class, maxRow = 0)})
public class AppleServiceImpl extends FruitService {

    @Override
    @Logging(maxLength = 3, throwableLog = {
            @ThrowableLog(throwable = RuntimeException.class, maxRow = 1),
            @ThrowableLog(throwable = {NullPointerException.class, IllegalArgumentException.class}, maxRow = 5)})
    public void printName() {
        super.printName();
        log.info("appleInner: My name is Apple");
        log.error("This class printInnerException maxRow is 1", new RuntimeException("This is RunTimeException"));
        log.error("This class printInnerException maxRow is 5", new IllegalArgumentException("This is IllegalArgumentException"));
        log.error("This class printInnerException maxRow is 3", new NullPointerException("This is NullPointerException"));
    }
}
