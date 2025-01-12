package com.gracerun.idempotent.aspect;

import com.gracerun.idempotent.annotation.Idempotent;
import com.gracerun.idempotent.exception.IdempotentException;
import com.gracerun.idempotent.pojo.IdempotentEntity;
import com.gracerun.idempotent.repository.IdempotentRepository;
import com.gracerun.tool.util.SpelParameterParser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.expression.EvaluationContext;

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
        String[] paramNames = SpelParameterParser.getParameterNames(specificMethod);
        EvaluationContext context = SpelParameterParser.buildContext(paramNames, pjp.getArgs());
        String primaryNo = SpelParameterParser.parse(idempotent.primaryNo(), context);
        String primaryType = SpelParameterParser.parse(idempotent.primaryType(), context);

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

}
