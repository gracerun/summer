

package com.summer.log.interceptor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.lang.Nullable;

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
