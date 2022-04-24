package com.summer.log.interceptor;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractFallbackLoggingAttributeSource implements LoggingAttributeSource, EmbeddedValueResolverAware {

    @Nullable
    private transient StringValueResolver embeddedValueResolver;

    private final Map<Object, LoggingAttribute> attributeCache = new ConcurrentHashMap<>(1024);

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
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
            return cached;
        } else {
            LoggingAttribute logAttr = computeLoggingAttribute(method, targetClass);
            if (logAttr != null) {
                this.attributeCache.put(cacheKey, logAttr);
            }
            return logAttr;
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
        LoggingAttribute txAttr = findLoggingAttribute(specificMethod);
        if (txAttr != null) {
            return txAttr;
        }

        // Second try is the logging attribute on the target class.
        txAttr = findLoggingAttribute(specificMethod.getDeclaringClass());
        if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
            return txAttr;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            txAttr = findLoggingAttribute(method);
            if (txAttr != null) {
                return txAttr;
            }
            // Last fallback is the class of the original method.
            txAttr = findLoggingAttribute(method.getDeclaringClass());
            if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
                return txAttr;
            }
        }

        return null;
    }

    @Nullable
    protected abstract LoggingAttribute findLoggingAttribute(Class<?> clazz);

    @Nullable
    protected abstract LoggingAttribute findLoggingAttribute(Method method);

    protected boolean allowPublicMethodsOnly() {
        return false;
    }

}
