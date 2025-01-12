package com.gracerun.log.annotation;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.annotation.*;

/**
 * 日志记录
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoggingPointCut {

    /**
     * 切点表达式
     *
     * @return
     * @see AspectJExpressionPointcut
     */
    String expression() default "";

    /**
     * 日志配置
     *
     * @return
     */
    Logging logging() default @Logging;

}
