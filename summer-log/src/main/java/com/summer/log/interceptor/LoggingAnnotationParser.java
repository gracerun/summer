package com.summer.log.interceptor;

import com.summer.log.annotation.Logging;
import com.summer.log.annotation.ThrowableLog;
import com.summer.log.constant.Level;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
        LoggingAttribute loggingAttribute = new LoggingAttribute();
        loggingAttribute.setName(logging.name());
        loggingAttribute.setLevel(logging.level());
        if (Level.OFF == logging.level()) {
            return null;
        }

        if (StringUtils.hasText(loggingAttribute.getName())) {
            loggingAttribute.setTargetLog(LoggerFactory.getLogger(loggingAttribute.getName()));
        } else {
            loggingAttribute.setTargetLog(LoggerFactory.getLogger(clazz));
        }

        loggingAttribute.setMaxLength(logging.maxLength());

        loggingAttribute.setSerializeArgsUsing(BeanUtils.instantiateClass(logging.serializeArgsUsing()));
        loggingAttribute.setSerializeReturnUsing(BeanUtils.instantiateClass(logging.serializeReturnUsing()));

        final ThrowableLog[] throwableLogs = logging.throwableLog();
        if (throwableLogs.length > 0) {
            final List<ThrowableLogAttribute> throwableLogAttributes = new ArrayList<>(throwableLogs.length);
            for (int i = 0; i < throwableLogs.length; i++) {
                final ThrowableLogAttribute throwableLogAttribute = new ThrowableLogAttribute();
                throwableLogAttribute.setThrowable(throwableLogs[i].throwable());
                throwableLogAttribute.setMaxRow(throwableLogs[i].maxRow());
                throwableLogAttributes.add(throwableLogAttribute);
            }
            loggingAttribute.setThrowableLogAttributes(throwableLogAttributes);
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
