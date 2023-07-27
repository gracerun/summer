package com.gracerun.log.interceptor;

import cn.hutool.core.lang.Singleton;
import com.gracerun.log.annotation.Logging;
import com.gracerun.log.annotation.ThrowableLog;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

public class LoggingAnnotationParser {

    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, Logging.class);
    }

    @Nullable
    public LoggingAttribute parseLoggingAnnotation(AnnotatedElement element, Class<?> clazz) {
        final Logging logging = AnnotatedElementUtils.findMergedAnnotation(element, Logging.class);
        if (logging != null) {
            return parseLoggingAnnotation(logging, clazz);
        } else {
            return null;
        }
    }

    protected LoggingAttribute parseLoggingAnnotation(Logging logging, Class<?> clazz) {
        RuleBasedLoggingAttribute loggingAttribute = new RuleBasedLoggingAttribute();
        loggingAttribute.setName(logging.name());
        loggingAttribute.setLevel(logging.level());

        if (StringUtils.hasText(loggingAttribute.getName())) {
            loggingAttribute.setTargetLog(LoggerFactory.getLogger(loggingAttribute.getName()));
        } else {
            loggingAttribute.setTargetLog(LoggerFactory.getLogger(clazz));
        }

        loggingAttribute.setMaxLength(logging.maxLength());
        loggingAttribute.setSerializeArgsUsing(Singleton.get(logging.serializeArgsUsing()));
        loggingAttribute.setSerializeReturnUsing(Singleton.get(logging.serializeReturnUsing()));

        final ThrowableLog[] throwableLogs = logging.throwableLog();
        if (throwableLogs.length > 0) {
            final List<ThrowableLogRuleAttribute> logRuleAttributes = new ArrayList<>(throwableLogs.length);
            for (int i = 0; i < throwableLogs.length; i++) {
                Class<? extends Throwable>[] throwable = throwableLogs[i].throwable();
                if (throwable.length > 0) {
                    for (int j = 0; j < throwable.length; j++) {
                        logRuleAttributes.add(new ThrowableLogRuleAttribute(throwable[j], throwableLogs[i].maxRow()));
                    }
                }
            }
            loggingAttribute.setLogRuleAttributes(logRuleAttributes);
        }
        return loggingAttribute;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (other instanceof LoggingAnnotationParser);
    }

    @Override
    public int hashCode() {
        return LoggingAnnotationParser.class.hashCode();
    }

}
