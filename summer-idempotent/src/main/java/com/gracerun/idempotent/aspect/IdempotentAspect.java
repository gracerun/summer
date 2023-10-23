package com.gracerun.idempotent.aspect;

import com.gracerun.idempotent.annotation.Idempotent;
import com.gracerun.idempotent.exception.IdempotentException;
import com.gracerun.idempotent.pojo.IdempotentEntity;
import com.gracerun.idempotent.repository.IdempotentRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 幂等校验拦截器
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/15
 */
@Aspect
@Order
@Slf4j
public class IdempotentAspect {

    @Resource
    public IdempotentRepository idempotentRepository;

    @Around("@annotation(idempotent)")
    public Object aroundAdvice(ProceedingJoinPoint pjp, Idempotent idempotent) throws Throwable {
        Class<?> targetClass = (pjp.getThis() != null ? AopUtils.getTargetClass(pjp.getThis()) : null);
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        String[] paramNames = IdempotentAnnotationProvider.getParameterNames(specificMethod);
        EvaluationContext context = buildContext(paramNames, pjp.getArgs());
        String primaryNo = IdempotentAnnotationProvider.parse(idempotent.primaryNo(), context);
        String primaryType = IdempotentAnnotationProvider.parse(idempotent.primaryType(), context);

        IdempotentEntity entity = IdempotentEntity.builder()
                .createTime(new Date())
                .primaryNo(primaryNo)
                .primaryType(primaryType)
                .build();

        boolean exists = idempotentRepository.exists(entity);
        if (exists) {
            throw new IdempotentException(primaryNo + ", " + primaryType + " already exists");
        } else {
            try {
                idempotentRepository.create(entity);
            } catch (DuplicateKeyException e) {
                throw new IdempotentException(primaryNo + ", " + primaryType + " already exists");
            }
        }
        return pjp.proceed();
    }

    /**
     * <p>
     * 根据参数名和参数值构建EvaluationContext
     * </p>
     *
     * @param paramNames  参数名称集合
     * @param paramValues 参数值集合
     * @return org.springframework.expression.EvaluationContext
     */
    private EvaluationContext buildContext(String[] paramNames, Object[] paramValues) {
        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames == null || paramValues == null) {
            return context;
        }
        for (int i = 0; i < paramValues.length; i++) {
            context.setVariable(paramNames[i], paramValues[i]);
        }
        return context;
    }

}
