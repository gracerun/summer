package com.summer.log.interceptor;

import com.summer.log.annotation.Logging;
import com.summer.log.serializer.LogSerializer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
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
        AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
                element, Logging.class, false, false);
        if (attributes != null) {
            return parseLoggingAnnotation(attributes, clazz);
        } else {
            return null;
        }
    }

    protected LoggingAttribute parseLoggingAnnotation(AnnotationAttributes attributes, Class<?> clazz) {
        LoggingAttribute loggingAttribute = new LoggingAttribute();
        loggingAttribute.setName(attributes.getString("name"));

        if (StringUtils.hasText(loggingAttribute.getName())) {
            loggingAttribute.setTargetLog(LoggerFactory.getLogger(loggingAttribute.getName()));
        } else {
            loggingAttribute.setTargetLog(LoggerFactory.getLogger(clazz));
        }

        loggingAttribute.setLevel(attributes.getEnum("level"));
        loggingAttribute.setMaxLength(attributes.getNumber("maxLength").intValue());

        final Class<?> serializeArgsUsing = attributes.getClass("serializeArgsUsing");
        final Class<?> serializeReturnUsing = attributes.getClass("serializeReturnUsing");

        loggingAttribute.setSerializeArgsUsing((LogSerializer) BeanUtils.instantiateClass(serializeArgsUsing));
        loggingAttribute.setSerializeReturnUsing((LogSerializer) BeanUtils.instantiateClass(serializeReturnUsing));

        final AnnotationAttributes[] throwableLogs = attributes.getAnnotationArray("throwableLog");
        final List<ThrowableLogAttribute> throwableLogAttributes = new ArrayList<>(throwableLogs.length);
        for (int i = 0; i < throwableLogs.length; i++) {
            final ThrowableLogAttribute throwableLogAttribute = new ThrowableLogAttribute();
            throwableLogAttribute.setThrowable(throwableLogs[i].getClass("throwable"));
            throwableLogAttribute.setMaxRow(throwableLogs[i].getNumber("maxRow").intValue());
            throwableLogAttributes.add(throwableLogAttribute);
        }
        loggingAttribute.setThrowableLogAttributes(throwableLogAttributes);
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
