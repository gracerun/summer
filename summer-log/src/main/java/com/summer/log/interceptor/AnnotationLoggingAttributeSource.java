package com.summer.log.interceptor;

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

public class AnnotationLoggingAttributeSource extends AbstractFallbackLoggingAttributeSource
        implements Serializable {

    private final boolean publicMethodsOnly;

    private final Set<LoggingAnnotationParser> annotationParsers;

    public AnnotationLoggingAttributeSource() {
        this(false);
    }

    public AnnotationLoggingAttributeSource(boolean publicMethodsOnly) {
        this.publicMethodsOnly = publicMethodsOnly;
        this.annotationParsers = Collections.singleton(new LoggingAnnotationParser());
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
    protected LoggingAttribute findLoggingAttribute(Class<?> clazz) {
        return determineLoggingAttribute(clazz, clazz);
    }

    @Override
    @Nullable
    protected LoggingAttribute findLoggingAttribute(Method method) {
        return determineLoggingAttribute(method, method.getDeclaringClass());
    }

    @Nullable
    protected LoggingAttribute determineLoggingAttribute(AnnotatedElement element, Class<?> clazz) {
        for (LoggingAnnotationParser parser : this.annotationParsers) {
            LoggingAttribute attr = parser.parseLoggingAnnotation(element, clazz);
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
