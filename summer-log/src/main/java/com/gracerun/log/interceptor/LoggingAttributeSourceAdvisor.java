package com.gracerun.log.interceptor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.lang.Nullable;

/**
 * @author Tom
 * @version 1.0.0
 * @date 2023/8/4
 */
public class LoggingAttributeSourceAdvisor extends AbstractPointcutAdvisor {

    @Nullable
    private LoggingAttributeSource loggingAttributeSource;

    @Nullable
    private LoggingInterceptor loggingInterceptor;

    private final LoggingAttributeSourcePointcut pointcut = new LoggingAttributeSourcePointcut() {
        @Override
        @Nullable
        protected LoggingAttributeSource getLoggingAttributeSource() {
            return loggingAttributeSource;
        }
    };

    public void setLoggingAttributeSource(LoggingAttributeSource loggingAttributeSource) {
        this.loggingAttributeSource = loggingAttributeSource;
    }

    public void setLoggingInterceptor(@Nullable LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return loggingInterceptor;
    }
}
