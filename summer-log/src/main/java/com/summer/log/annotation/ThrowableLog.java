package com.summer.log.annotation;

/**
 * 异常日志
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/20/22
 */
public @interface ThrowableLog {

    /**
     * 记录异常类
     *
     * @return
     */
    Class<? extends Throwable> printThrowable();

    /**
     * 异常日志最大行数
     * -1:不限制行数
     * 0:不记录异常
     *
     * @return
     */
    int printMaxRow() default -1;

}
