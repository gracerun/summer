package com.test.message.service;

import com.summer.log.annotation.Logging;
import com.summer.log.annotation.ThrowableLog;
import com.summer.log.constant.Level;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 基础类
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
@Logging(throwableLog = {@ThrowableLog(throwable = Throwable.class, maxRow = 5)})
public abstract class BaseService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Logging(level = Level.OFF)
    public void print() {
        try {
            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 3));
        } catch (InterruptedException e) {

        }
        log.info("-------print----------------------------{}", System.currentTimeMillis());
        try {
            printAAA();
        } catch (Throwable e) {
            throw new RuntimeException("printAAA报错了", e);
        }
        log.info("-------print----------------------------{}", System.currentTimeMillis());
    }

    public abstract String printAAA();
}
