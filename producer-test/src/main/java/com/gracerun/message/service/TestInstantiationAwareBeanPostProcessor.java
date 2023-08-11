//package com.gracerun.message.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.PropertyValues;
//import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class TestInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
//
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        log.info("{} [TestInstantiationAwareBeanPostProcessor] before initialization " + beanName, AtomicIntegerTest.increment());
//        return bean;
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        log.info("{} [TestInstantiationAwareBeanPostProcessor] after initialization " + beanName, AtomicIntegerTest.increment());
//        return bean;
//    }
//
//    @Override
//    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
//        log.info("{} [TestInstantiationAwareBeanPostProcessor] before instantiation " + beanName, AtomicIntegerTest.increment());
//        return null;
//    }
//
//    @Override
//    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
//        log.info("{} [TestInstantiationAwareBeanPostProcessor] after instantiation " + beanName, AtomicIntegerTest.increment());
//        return true;
//    }
//
//    @Override
//    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
//        log.info("{} [TestInstantiationAwareBeanPostProcessor] postProcessProperties " + beanName, AtomicIntegerTest.increment());
//        return pvs;
//    }
//
//}
