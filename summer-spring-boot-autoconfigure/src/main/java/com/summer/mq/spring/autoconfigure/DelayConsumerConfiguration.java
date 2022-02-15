package com.summer.mq.spring.autoconfigure;

import com.summer.mq.annotation.SummerMQMessageListener;
import com.summer.mq.consumer.MessageDelayPullConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class DelayConsumerConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private final static Logger log = LoggerFactory.getLogger(DelayConsumerConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    private AtomicLong counter = new AtomicLong(0);

    private StandardEnvironment environment;

    public DelayConsumerConfiguration(StandardEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(SummerMQMessageListener.class);

        Set<String> groupNames = new HashSet<>();
        if (Objects.nonNull(beans)) {
            beans.forEach((k, v) -> {
                Class<?> clazz = AopProxyUtils.ultimateTargetClass(v);
                SummerMQMessageListener annotation = clazz.getAnnotation(SummerMQMessageListener.class);
                groupNames.add(this.environment.resolvePlaceholders(annotation.consumerGroup()));
            });
            groupNames.forEach(this::registerContainer);
        }
    }

    private void registerContainer(String groupName) {
        String beanName = String.format("%s_%s", MessageDelayPullConsumer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;

        BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(MessageDelayPullConsumer.class);
        beanBuilder.addPropertyValue("consumerGroup", groupName);

        genericApplicationContext.registerBeanDefinition(beanName, beanBuilder.getBeanDefinition());
        genericApplicationContext.getBean(beanName, MessageDelayPullConsumer.class);
        log.info("Register the delay consumer, beanName:{}", beanName);
    }

}
