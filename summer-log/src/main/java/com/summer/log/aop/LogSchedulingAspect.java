

package com.summer.log.aop;

import com.summer.log.constant.LogCategoryConstant;
import com.summer.log.filter.LogCategoryFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;

/**
 * 设置定时日志类别
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Aspect
public class LogSchedulingAspect {

    private static final Integer INDEX_INIT_VALUE = 0;

    private static final ThreadLocal<Integer> METHOD_LEVEL = ThreadLocal.withInitial(() -> INDEX_INIT_VALUE);

    @Around("execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        final Integer methodLevel = METHOD_LEVEL.get();
        try {
            if (INDEX_INIT_VALUE.equals(methodLevel)) {
                METHOD_LEVEL.set(methodLevel + 1);
                MDC.put(LogCategoryFilter.LOG_CATEGORY_NAME, LogCategoryConstant.SCHEDULED);
            }
            return pjp.proceed();
        } finally {
            if (INDEX_INIT_VALUE.equals(methodLevel)) {
                MDC.remove(LogCategoryFilter.LOG_CATEGORY_NAME);
                METHOD_LEVEL.set(methodLevel);
            }
        }
    }

}
