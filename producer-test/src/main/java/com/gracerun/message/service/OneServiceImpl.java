package com.gracerun.message.service;

import com.gracerun.log.annotation.Logging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class OneServiceImpl {

    @Resource
    private TwoServiceImpl twoService;

    @Logging
    public void printException() {
        twoService.printException();
    }
}
