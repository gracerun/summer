package com.test.message.service;

import com.summer.log.annotation.Logging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Logging
public class BBBServiceImpl {

    public String printBBB() {
        log.info("-------{}", System.currentTimeMillis());
        return "SUCCESS";
    }
}
