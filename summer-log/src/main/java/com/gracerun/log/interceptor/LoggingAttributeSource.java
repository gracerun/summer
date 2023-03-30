

package com.gracerun.log.interceptor;

import java.lang.reflect.Method;

public interface LoggingAttributeSource {

    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    LoggingAttribute getLoggingAttribute(Method method, Class<?> targetClass);

}
