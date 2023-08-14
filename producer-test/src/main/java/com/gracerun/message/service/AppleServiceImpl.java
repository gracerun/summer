package com.test.message.service;

import com.gracerun.log.annotation.Logging;
import com.gracerun.log.annotation.ThrowableLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Logging(throwableLog = {@ThrowableLog(throwable = RuntimeException.class, maxRow = 0)})
public class AppleServiceImpl extends FruitService {

    @Override
    @Logging(maxLength = 3)
    public void printName() {
        super.printName();
        log.info("appleInner: My name is Apple");
        log.error("This class printInnerException maxRow is 0", new RuntimeException("This is RunTimeException"));
        String name = null;
        log.info(name.toLowerCase());
    }
}
