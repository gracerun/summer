package com.gracerun.log.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

abstract class LoggingAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

    protected LoggingAttributeSourcePointcut() {
        setClassFilter(new LoggingAttributeSourceClassFilter());
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        LoggingAttributeSource las = getLoggingAttributeSource();
        return (las != null && las.getLoggingAttribute(method, targetClass) != null);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoggingAttributeSourcePointcut)) {
            return false;
        }
        LoggingAttributeSourcePointcut otherPc = (LoggingAttributeSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(getLoggingAttributeSource(), otherPc.getLoggingAttributeSource());
    }

    @Override
    public int hashCode() {
        return LoggingAttributeSourcePointcut.class.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getLoggingAttributeSource();
    }

    @Nullable
    protected abstract LoggingAttributeSource getLoggingAttributeSource();

    private class LoggingAttributeSourceClassFilter implements ClassFilter {

        @Override
        public boolean matches(Class<?> clazz) {
            LoggingAttributeSource las = getLoggingAttributeSource();
            return (las == null || las.isCandidateClass(clazz));
        }
    }

}
