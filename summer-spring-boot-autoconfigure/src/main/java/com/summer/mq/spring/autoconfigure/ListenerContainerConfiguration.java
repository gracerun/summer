package com.summer.mq.spring.autoconfigure;

import com.summer.mq.annotation.SummerMQMessageListener;
import com.summer.mq.consumer.MessageListener;
import com.summer.mq.consumer.RedisMessagePullConsumer;
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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ListenerContainerConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private final static Logger log = LoggerFactory.getLogger(ListenerContainerConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    private AtomicLong counter = new AtomicLong(0);

    private StandardEnvironment environment;

    public ListenerContainerConfiguration(StandardEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(SummerMQMessageListener.class);

        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerContainer);
        }
    }

    private void registerContainer(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        if (!MessageListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + MessageListener.class.getName());
        }

        SummerMQMessageListener annotation = clazz.getAnnotation(SummerMQMessageListener.class);
        String containerBeanName = String.format("%s_%s", RedisMessagePullConsumer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;

        BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(RedisMessagePullConsumer.class);
        beanBuilder.addConstructorArgValue(this.environment.resolvePlaceholders(annotation.topic()));
        beanBuilder.addPropertyValue("consumerNamespace", this.environment.resolvePlaceholders(annotation.consumerNamespace()));
        beanBuilder.addPropertyValue("batchRpopSize", annotation.batchRpopSize());
        beanBuilder.addPropertyValue("corePoolSize", annotation.corePoolSize());
        beanBuilder.addPropertyValue("maximumPoolSize", annotation.maximumPoolSize());
        beanBuilder.addPropertyValue("keepAliveTime", annotation.keepAliveTime());
        beanBuilder.addPropertyValue("blockingQueueSize", annotation.blockingQueueSize());
        beanBuilder.addPropertyValue("summerMQMessageListener", annotation);
        beanBuilder.addPropertyValue("messageListener", bean);

        genericApplicationContext.registerBeanDefinition(containerBeanName, beanBuilder.getBeanDefinition());
        RedisMessagePullConsumer messagePullConsumer = genericApplicationContext.getBean(containerBeanName,
                RedisMessagePullConsumer.class);
        messagePullConsumer.start();

        log.info("Register the listener to messagePullConsumer, listenerBeanName:{}, containerBeanName:{}", beanName, containerBeanName);
    }

}
