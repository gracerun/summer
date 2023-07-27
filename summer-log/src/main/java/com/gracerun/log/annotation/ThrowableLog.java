package com.gracerun.log.annotation;

/**
 * 异常日志
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/20/22
 */
public @interface ThrowableLog {

    /**
     * 限定异常类及其所有子类
     *
     * @return
     */
    Class<? extends Throwable>[] throwable();

    /**
     * 打印异常堆栈的行数不超过maxRow
     * -1:不限制行数
     * 0:不打印异常堆栈信息
     *
     * @return
     */
    int maxRow() default -1;

}
