package com.gracerun.message.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NormalBeanA implements BeanNameAware {
    public NormalBeanA() {
        log.info("NormalBean constructor");
    }

    @Override
    public void setBeanName(String name) {
        log.info("[BeanNameAware] " + name);
    }
}
