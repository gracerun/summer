

package com.summer.log.aop;

import com.summer.log.constant.LogCategoryConstant;
import com.summer.log.filter.LogCategoryFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * 设置定时日志类别
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Aspect
public class LogSchedulingAspect {

    private static final ThreadLocal<String> categoryNameHolder = new ThreadLocal<>();

    @Around("execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        final String categoryName = categoryNameHolder.get();
        try {
            if (Objects.isNull(categoryName)) {
                categoryNameHolder.set(LogCategoryConstant.SCHEDULED);
                MDC.put(LogCategoryFilter.LOG_CATEGORY_NAME, LogCategoryConstant.SCHEDULED);
            }
            return pjp.proceed();
        } finally {
            if (Objects.isNull(categoryName)) {
                MDC.remove(LogCategoryFilter.LOG_CATEGORY_NAME);
                categoryNameHolder.remove();
            }
        }
    }

}
