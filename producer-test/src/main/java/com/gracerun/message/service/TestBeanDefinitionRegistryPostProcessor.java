//package com.gracerun.message.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class TestBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//        log.info("{} [BeanDefinitionRegistryPostProcessor] postProcessBeanDefinitionRegistry", AtomicIntegerTest.increment());
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        log.info("{} [BeanDefinitionRegistryPostProcessor] postProcessBeanFactory", AtomicIntegerTest.increment());
//    }
//}
