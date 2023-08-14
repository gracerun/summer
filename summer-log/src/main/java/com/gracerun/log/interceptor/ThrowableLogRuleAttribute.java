package com.gracerun.log.interceptor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
public class ThrowableLogRuleAttribute {

    private final String exceptionPattern;

    /**
     * 异常日志最大行数
     * -1:不限制行数
     * 0:不记录异常
     */
    private final int maxRow;

    public ThrowableLogRuleAttribute(Class<?> exceptionType, int maxRow) {
        Assert.notNull(exceptionType, "'exceptionType' cannot be null");

        if (!Throwable.class.isAssignableFrom(exceptionType)) {
            throw new IllegalArgumentException("Cannot construct rollback rule from [" + exceptionType.getName() + "]: it's not a Throwable");
        }
        this.exceptionPattern = exceptionType.getName();
        this.maxRow = maxRow;
    }

    public int getDepth(Throwable exception) {
        return getDepth(exception.getClass(), 0);
    }


    private int getDepth(Class<?> exceptionType, int depth) {

        if (exceptionType.getName().contains(this.exceptionPattern)) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionType == Throwable.class) {
            return -1;
        }
        return getDepth(exceptionType.getSuperclass(), depth + 1);
    }

}