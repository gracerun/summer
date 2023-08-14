package com.gracerun.message.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestSmartInitializingSingleton implements SmartInitializingSingleton {
    @Override
    public void afterSingletonsInstantiated() {
        log.info("{} [TestSmartInitializingSingleton]", AtomicIntegerTest.increment());
    }
}
