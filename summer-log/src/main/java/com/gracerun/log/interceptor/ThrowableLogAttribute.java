package com.gracerun.log.interceptor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThrowableLogAttribute {

    /**
     * 记录异常类
     */
    Class<? extends Throwable> throwable;

    /**
     * 异常日志最大行数
     * -1:不限制行数
     * 0:不记录异常
     */
    int maxRow;

}