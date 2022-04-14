package com.summer.log.aop;

import com.summer.log.constant.MDCConstant;
import com.summer.log.core.RequestInfo;
import com.summer.log.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;

/**
 * 日志记录
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LoggingAspect {

    private static final Integer INDEX_INIT_VALUE = 0;

    private static final ThreadLocal<Integer> THREAD_LOCAL = ThreadLocal.withInitial(() -> INDEX_INIT_VALUE);

    @Around("@within(com.summer.log.annotation.Logging) || @annotation(com.summer.log.annotation.Logging)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        final Integer methodLevel = THREAD_LOCAL.get();
        try {
            long start = System.currentTimeMillis();
            THREAD_LOCAL.set(methodLevel + 1);
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            MDC.put(MDCConstant.CURRENT_METHOD_NAME, signature.getName());
            MDC.put(MDCConstant.METHOD_NAME + methodLevel, signature.getName());
            Class targetClass;
            if (pjp.getTarget().getClass().getName().startsWith("com.sun")) {
                targetClass = pjp.getSignature().getDeclaringType();
            } else {
                targetClass = pjp.getTarget().getClass();
            }
            Logger targetLog = LoggerFactory.getLogger(targetClass);

            RequestInfo requestInfo = getRequestInfo();
            if (INDEX_INIT_VALUE.equals(methodLevel) && Objects.nonNull(requestInfo)) {
                targetLog.info("requestInfo: RemoteHost={}, RequestType={}", requestInfo.getRemoteHost(), requestInfo.getRequestType());
            }
            targetLog.info("begin - {}", Arrays.toString(pjp.getArgs()));

            try {
                Object proceed = pjp.proceed();
                long responseTime = System.currentTimeMillis() - start;
                Class<?> returnType = signature.getReturnType();
                info(targetLog, responseTime, returnType, proceed);
                return proceed;
            } catch (Exception e) {
                targetLog.error(e.getMessage(), e);
                throw e;
            }
        } finally {
            MDC.remove(MDCConstant.METHOD_NAME + methodLevel);
            if (INDEX_INIT_VALUE.equals(methodLevel)) {
                MDC.remove(MDCConstant.CURRENT_METHOD_NAME);
            } else {
                MDC.put(MDCConstant.CURRENT_METHOD_NAME, MDC.get(MDCConstant.METHOD_NAME + (methodLevel - 1)));
            }
            THREAD_LOCAL.set(methodLevel);
        }
    }

    /**
     * 日志格式： 日期时间 日志级别 接口类 接口方法 起止标志 响应时间 详细描述
     *
     * @param logger
     * @param responseTime
     * @param message
     */
    private void info(Logger logger, long responseTime, Class<?> returnType, Object message) {
        if (Void.TYPE != returnType) {
            logger.info("end {}ms {}", responseTime, message);
        } else {
            logger.info("end {}ms", responseTime);
        }
    }

    /**
     * 获取当前请求IP和请求接口类型
     *
     * @return
     */
    private RequestInfo getRequestInfo() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        if (sra != null && sra.getRequest() != null) {
            return new RequestInfo(NetworkUtil.getIpAddress(sra.getRequest()), sra.getRequest().getScheme());
        } else {
            return null;
        }
    }

}
