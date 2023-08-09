package com.gracerun.message.service;//package com.yee.common.rocketmq.log;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Constructor;
//
//@Component
//@Slf4j
//public class TestSmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
//
//    @Override
//    public Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
//        log.info("{} [TestSmartInstantiationAwareBeanPostProcessor] predictBeanType " + beanName, AtomicIntegerTest.increment());
//        return beanClass;
//    }
//
//    @Override
//    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
//        log.info("{} [TestSmartInstantiationAwareBeanPostProcessor] determineCandidateConstructors " + beanName, AtomicIntegerTest.increment());
//        return null;
//    }
//
//    @Override
//    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
//        log.info("{} [TestSmartInstantiationAwareBeanPostProcessor] getEarlyBeanReference " + beanName, AtomicIntegerTest.increment());
//        return bean;
//    }
//}
