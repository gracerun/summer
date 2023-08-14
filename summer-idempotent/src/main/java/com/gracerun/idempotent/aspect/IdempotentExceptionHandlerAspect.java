

package com.gracerun.idempotent.aspect;

import cn.hutool.core.lang.Singleton;
import com.gracerun.idempotent.annotation.Idempotent;
import com.gracerun.idempotent.exception.IdempotentException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 异常处理
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/15
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 301)
@Slf4j
public class IdempotentExceptionHandlerAspect {

    @Around("@annotation(idempotent)")
    public Object aroundAdvice(ProceedingJoinPoint pjp, Idempotent idempotent) throws Throwable {
        try {
            return pjp.proceed();
        } catch (IdempotentException e) {
            return Singleton.get(idempotent.handler()).rejectedExecution(pjp.getArgs(), e);
        }
    }

}
