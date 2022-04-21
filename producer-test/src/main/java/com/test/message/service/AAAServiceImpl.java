package com.test.message.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AAAServiceImpl extends BaseService {

    @Autowired
    private BBBServiceImpl bbbService;

    @Override
    public String printAAA() {
        try {
            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 3));
        } catch (InterruptedException e) {

        }
        log.info("-------AAA{}", System.currentTimeMillis());
        bbbService.printBBB();
        log.info("-------AAA{}", System.currentTimeMillis());
        return "SUCCESS";
    }
}
