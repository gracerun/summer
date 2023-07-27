package com.gracerun.message.service;

import com.gracerun.log.annotation.Logging;
import com.gracerun.log.annotation.ThrowableLog;

@Logging(throwableLog = {@ThrowableLog(throwable = Throwable.class, maxRow = 10)})
public interface FruitInterface {

    @Logging(throwableLog = {@ThrowableLog(throwable = Throwable.class, maxRow = 1)})
    void printName();
}
