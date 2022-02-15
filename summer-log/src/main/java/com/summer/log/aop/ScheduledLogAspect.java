

package com.summer.log.aop;

import com.summer.log.constant.LogCategoryConstant;
import com.summer.log.filter.LogCategoryFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 设置定时日志类别
 * @author Tom
 * @date 12/26/21
 * @version 1.0.0
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ScheduledLogAspect {

    @Around("execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        try {
            MDC.put(LogCategoryFilter.LOG_CATEGORY_NAME, LogCategoryConstant.SCHEDULED);
            return pjp.proceed();
        } finally {
            MDC.remove(LogCategoryFilter.LOG_CATEGORY_NAME);
        }
    }

}
