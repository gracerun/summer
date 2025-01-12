package com.gracerun.log.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AnnotationLoggingAttributeSource extends AbstractFallbackLoggingAttributeSource {

    private final boolean publicMethodsOnly;

    private final Set<LoggingAnnotationParser> annotationParsers;

    private final List<LoggingAopAttribute> loggingAopAttributes;

    public AnnotationLoggingAttributeSource() {
        this(false);
    }

    public AnnotationLoggingAttributeSource(boolean publicMethodsOnly) {
        this(publicMethodsOnly, Collections.singleton(new LoggingAnnotationParser()), null);
    }

    public AnnotationLoggingAttributeSource(boolean publicMethodsOnly, Set<LoggingAnnotationParser> annotationParsers, List<LoggingAopAttribute> loggingAopAttributes) {
        this.publicMethodsOnly = publicMethodsOnly;
        this.annotationParsers = annotationParsers;
        this.loggingAopAttributes = loggingAopAttributes;
    }

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        for (LoggingAnnotationParser parser : this.annotationParsers) {
            if (parser.isCandidateClass(targetClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    protected LoggingAttribute findLoggingAttribute(Class<?> clazz, Class<?> userClazz) {
        return determineLoggingAttribute(clazz, userClazz);
    }

    @Override
    @Nullable
    protected LoggingAttribute findLoggingAttribute(Method method, Class<?> userClazz) {
        return determineLoggingAttribute(method, userClazz);
    }

    @Nullable
    @Override
    protected LoggingAttribute findGlobalLoggingAttribute(Method method, Class<?> userClazz) {
        if (!CollectionUtils.isEmpty(loggingAopAttributes)) {
            for (LoggingAopAttribute e : loggingAopAttributes) {
                if (e.getPointcut().getMethodMatcher().matches(method, userClazz)) {
                    return e.getLoggingAttribute();
                }
            }
        }
        return null;
    }

    @Nullable
    protected LoggingAttribute determineLoggingAttribute(AnnotatedElement element, Class<?> userClazz) {
        for (LoggingAnnotationParser parser : this.annotationParsers) {
            LoggingAttribute attr = parser.parseLoggingAnnotation(element, userClazz);
            if (attr != null) {
                return attr;
            }
        }
        return null;
    }

    @Override
    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodsOnly;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationLoggingAttributeSource)) {
            return false;
        }
        AnnotationLoggingAttributeSource otherTas = (AnnotationLoggingAttributeSource) other;
        return (this.annotationParsers.equals(otherTas.annotationParsers) &&
                this.publicMethodsOnly == otherTas.publicMethodsOnly);
    }

    @Override
    public int hashCode() {
        return this.annotationParsers.hashCode();
    }

}
