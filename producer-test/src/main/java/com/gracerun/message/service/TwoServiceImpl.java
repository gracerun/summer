package com.gracerun.message.service;

import com.gracerun.log.annotation.Logging;
import com.gracerun.log.annotation.ThrowableLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class TwoServiceImpl {

    @Resource
    private ThreeServiceImpl threeService;

    @Logging(throwableLog = {@ThrowableLog(throwable =
            {IllegalArgumentException.class, IllegalStateException.class, RuntimeException.class}, maxRow = 1)})
    public void printException() {
        threeService.printException();
    }
}
