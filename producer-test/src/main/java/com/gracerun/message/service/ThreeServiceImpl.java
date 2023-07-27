package com.gracerun.message.service;

import com.gracerun.log.annotation.Logging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThreeServiceImpl {

    @Logging(throwableLog = {})
    public void printException() {
        throw new RuntimeException("报错了");
    }
}
