package com.gracerun.log.annotation;

import java.lang.annotation.*;

/**
 * 日志配置
 *
 * @author Tom
 * @version 1.0.0
 * @date 2024/3/11
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoggingAop {

    /**
     * 切点表达式
     *
     * @return
     */
    LoggingPointCut[] value();

}
