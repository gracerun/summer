package com.gracerun.log.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractFallbackLoggingAttributeSource implements LoggingAttributeSource, ApplicationContextAware {

    private final Map<Object, LoggingAttribute> attributeCache = new ConcurrentHashMap<>(1024);

    private static final LoggingAttribute NULL_LOGGING_ATTRIBUTE = new LoggingAttribute() {
        @Override
        public String toString() {
            return "null";
        }
    };

    private ConfigurableApplicationContext applicationContext;

    private String startAppPackageName;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @PostConstruct
    public void init() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        if (!CollectionUtils.isEmpty(beansWithAnnotation)) {
            beansWithAnnotation.forEach((k, v) -> {
                startAppPackageName = v.getClass().getPackage().getName();
                log.info("startAppPackageName={}", startAppPackageName);
            });
        }
    }

    @Override
    @Nullable
    public LoggingAttribute getLoggingAttribute(Method method, @Nullable Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }

        Object cacheKey = getCacheKey(method, targetClass);
        LoggingAttribute cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            if (cached == NULL_LOGGING_ATTRIBUTE) {
                return null;
            } else {
                return cached;
            }
        } else {
            if (!StringUtils.hasText(startAppPackageName)
                    || method.getDeclaringClass().getName().startsWith(startAppPackageName)) {
                LoggingAttribute logAttr = computeLoggingAttribute(method, targetClass);
                if (logAttr != null) {
                    String methodIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);
                    logAttr.setMethodIdentification(methodIdentification);
                    this.attributeCache.put(cacheKey, logAttr);
                    return logAttr;
                } else {
                    this.attributeCache.put(cacheKey, NULL_LOGGING_ATTRIBUTE);
                    return null;
                }
            } else {
                this.attributeCache.put(cacheKey, NULL_LOGGING_ATTRIBUTE);
                return null;
            }
        }
    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    @Nullable
    protected LoggingAttribute computeLoggingAttribute(Method method, @Nullable Class<?> targetClass) {
        // Don't allow non-public methods, as configured.
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        // First try is the method in the target class.
        LoggingAttribute logAttr = findLoggingAttribute(specificMethod, method.getDeclaringClass());
        if (logAttr != null) {
            return logAttr;
        }

        // Second try is the logging attribute on the target class.
        logAttr = findLoggingAttribute(specificMethod.getDeclaringClass(), method.getDeclaringClass());
        if (logAttr != null && ClassUtils.isUserLevelMethod(method)) {
            return logAttr;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            logAttr = findLoggingAttribute(method, method.getDeclaringClass());
            if (logAttr != null) {
                return logAttr;
            }
            // Fallback is the class of the original method.
            logAttr = findLoggingAttribute(method.getDeclaringClass(), method.getDeclaringClass());
            if (logAttr != null && ClassUtils.isUserLevelMethod(method)) {
                return logAttr;
            }
        }

        // 获取全局配置
        logAttr = findGlobalLoggingAttribute(specificMethod, method.getDeclaringClass());
        if (logAttr != null) {
            return logAttr;
        }
        return null;
    }

    @Nullable
    protected abstract LoggingAttribute findLoggingAttribute(Class<?> clazz, Class<?> userClazz);

    @Nullable
    protected abstract LoggingAttribute findLoggingAttribute(Method method, Class<?> userClazz);

    @Nullable
    protected abstract LoggingAttribute findGlobalLoggingAttribute(Method method, Class<?> userClazz);

    protected boolean allowPublicMethodsOnly() {
        return false;
    }

}
