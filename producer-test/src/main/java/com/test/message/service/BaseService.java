package com.test.message.service;

import com.summer.log.annotation.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础类
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
@Logging
public abstract class BaseService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void print() {
        log.info("-------{}", System.currentTimeMillis());
        printAAA();
    }

    public abstract String printAAA();
}
