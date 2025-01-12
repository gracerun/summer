package com.gracerun.log.interceptor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

@Getter
@Setter
public class LoggingAopAttribute {

    /**
     * 切点
     */
    private AspectJExpressionPointcut pointcut;

    /**
     * 日志属性
     */
    private LoggingAttribute loggingAttribute;

}