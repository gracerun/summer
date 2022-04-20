package com.summer.log.aop;

import com.summer.log.annotation.Logging;
import com.summer.log.annotation.ThrowableLog;
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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
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

    private static final ThreadLocal<MethodLoggingInfo> methodLoggingInfoHolder = new ThreadLocal();

    @Around("@within(com.summer.log.annotation.Logging) || @annotation(com.summer.log.annotation.Logging)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        if (!(pjp.getSignature() instanceof MethodSignature)) {
            return pjp.proceed();
        }

        MethodLoggingInfo loggingInfo = createMethodLoggingIfNecessary(pjp);
        try {
            printStartLog(loggingInfo, pjp);
            Object retVal = pjp.proceed();
            printEndLog(loggingInfo, retVal);
            return retVal;
        } catch (Throwable e) {
            printThrowable(loggingInfo, e);
            throw e;
        } finally {
            cleanupMethodLoggingInfo(loggingInfo);
        }
    }

    private void printStartLog(MethodLoggingInfo loggingInfo, ProceedingJoinPoint pjp) {
        if (Objects.isNull(loggingInfo.oldLoggingInfo)) {
            RequestInfo requestInfo = getRequestInfo();
            if (Objects.nonNull(requestInfo)) {
                log(loggingInfo, "requestIp:{}, scheme:{}", requestInfo.getIp(), requestInfo.getScheme());
            }
        }
        log(loggingInfo, "begin - {}", Arrays.toString(pjp.getArgs()));
    }

    private void printEndLog(MethodLoggingInfo loggingInfo, Object retVal) {
        if (Void.TYPE != loggingInfo.getReturnType()) {
            log(loggingInfo, "end {}ms {}", loggingInfo.getTotalTimeMillis(), retVal);
        } else {
            log(loggingInfo, "end {}ms", loggingInfo.getTotalTimeMillis());
        }
    }

    private void printThrowable(MethodLoggingInfo loggingInfo, Throwable e) {
        if (printCondition(loggingInfo, e)) {
            if (Objects.nonNull(loggingInfo.oldLoggingInfo)) {
                loggingInfo.oldLoggingInfo.throwable = e;
            }
            if (loggingInfo.throwable != e) {
                loggingInfo.targetLog.error(e.getMessage(), e);
            }
        }
    }

    private boolean printCondition(MethodLoggingInfo loggingInfo, Throwable e) {
        if (Objects.nonNull(loggingInfo.logging) && !ObjectUtils.isEmpty(loggingInfo.logging.throwableLog())) {
            final ThrowableLog[] throwableLogs = loggingInfo.logging.throwableLog();
            for (int i = 0; i < throwableLogs.length; i++) {
                if (throwableLogs[i].printThrowable().isInstance(e)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private void log(MethodLoggingInfo loggingInfo, String format, Object... arguments) {
        switch (loggingInfo.logging.level()) {
            case INFO:
                loggingInfo.targetLog.info(format, arguments);
                break;
            case WARN:
                loggingInfo.targetLog.warn(format, arguments);
                break;
            case ERROR:
                loggingInfo.targetLog.error(format, arguments);
                break;
            case DEBUG:
                loggingInfo.targetLog.debug(format, arguments);
                break;
            case TRACE:
                loggingInfo.targetLog.trace(format, arguments);
                break;
        }
    }

    protected MethodLoggingInfo createMethodLoggingIfNecessary(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        final Class<?> declaringClass = signature.getMethod().getDeclaringClass();

        Logging logging = signature.getMethod().getAnnotation(Logging.class);
        if (Objects.isNull(logging)) {
            logging = AnnotationUtils.findAnnotation(declaringClass, Logging.class);
        }

        Logger targetLog;
        if (StringUtils.hasText(logging.value())) {
            targetLog = LoggerFactory.getLogger(logging.value());
        } else {
            targetLog = LoggerFactory.getLogger(declaringClass);
        }

        MethodLoggingInfo loggingInfo = new MethodLoggingInfo(logging, targetLog, signature);
        loggingInfo.bindToThread();
        return loggingInfo;
    }

    protected void cleanupMethodLoggingInfo(@Nullable MethodLoggingInfo methodLoggingInfo) {
        if (methodLoggingInfo != null) {
            methodLoggingInfo.restoreThreadLocalStatus();
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

    protected static final class MethodLoggingInfo {

        @Nullable
        private final Logging logging;

        private final MethodSignature signature;

        private final Logger targetLog;

        private final StopWatch stopWatch;

        private Throwable throwable;

        @Nullable
        private MethodLoggingInfo oldLoggingInfo;

        public MethodLoggingInfo(@Nullable Logging logging, Logger targetLog, MethodSignature signature) {
            this.logging = logging;
            this.targetLog = targetLog;
            this.signature = signature;
            this.stopWatch = new StopWatch();
        }

        @Nullable
        public Logging getLogging() {
            return this.logging;
        }

        public Logger getTargetLog() {
            return targetLog;
        }

        public String getMethodName() {
            return signature.getName();
        }

        public Class<?> getReturnType() {
            return signature.getReturnType();
        }

        public long getTotalTimeMillis() {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            return stopWatch.getTotalTimeMillis();
        }

        private void bindToThread() {
            stopWatch.start();
            this.oldLoggingInfo = methodLoggingInfoHolder.get();
            MDC.put(MDCConstant.CURRENT_METHOD_NAME, getMethodName());
            methodLoggingInfoHolder.set(this);
        }

        private void restoreThreadLocalStatus() {
            methodLoggingInfoHolder.set(this.oldLoggingInfo);
            if (Objects.nonNull(oldLoggingInfo)) {
                MDC.put(MDCConstant.CURRENT_METHOD_NAME, oldLoggingInfo.getMethodName());
            }
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }

    }

}
