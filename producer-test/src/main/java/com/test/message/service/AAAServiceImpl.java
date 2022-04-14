package com.test.message.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AAAServiceImpl extends BaseService {

    @Autowired
    private BBBServiceImpl bbbService;

    @Override
    public String printAAA() {
        log.info("-------{}", System.currentTimeMillis());
        bbbService.printBBB();
        return "SUCCESS";
    }
}
