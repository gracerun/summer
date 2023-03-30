package com.gracerun.log.interceptor;

import com.gracerun.log.constant.Level;
import com.gracerun.log.constant.MDCConstant;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Objects;

/**
 * 日志记录
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
public class LoggingInterceptor implements MethodInterceptor {

    private static final ThreadLocal<LoggingInfo> loggingInfoHolder = new ThreadLocal();

    @Nullable
    private LoggingAttributeSource loggingAttributeSource;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        LoggingInfo loggingInfo = createLoggingIfNecessary(invocation);
        try {
            printStartLog(loggingInfo, invocation);
            Object retVal = invocation.proceed();
            printEndLog(loggingInfo, retVal);
            return retVal;
        } catch (Throwable e) {
            printThrowable(loggingInfo, e);
            throw e;
        } finally {
            cleanupLoggingInfo(loggingInfo);
        }
    }

    private void printStartLog(LoggingInfo loggingInfo, MethodInvocation invocation) {
        log(loggingInfo, "begin - {}", loggingInfo.getLoggingAttribute().getSerializeArgsUsing().write(invocation.getArguments()));
    }

    private void printEndLog(LoggingInfo loggingInfo, Object retVal) {
        if (Void.TYPE != loggingInfo.getReturnType()) {
            log(loggingInfo, "end {}ms {}",
                    loggingInfo.getTotalTimeMillis(),
                    loggingInfo.getLoggingAttribute().getSerializeReturnUsing().write(retVal));
        } else {
            log(loggingInfo, "end {}ms", loggingInfo.getTotalTimeMillis());
        }
    }

    private void printThrowable(LoggingInfo loggingInfo, Throwable e) {
        if (loggingInfo.getLoggingAttribute().getLevel() != Level.OFF && printCondition(loggingInfo, e)) {
            if (Objects.nonNull(loggingInfo.oldLoggingInfo)) {
                loggingInfo.oldLoggingInfo.throwable = e;
            }
            if (loggingInfo.throwable != e) {
                loggingInfo.getTargetLog().error(e.getMessage(), e);
            }
        }
    }

    private boolean printCondition(LoggingInfo loggingInfo, Throwable e) {
        if (Objects.nonNull(loggingInfo.loggingAttribute) && !ObjectUtils.isEmpty(loggingInfo.loggingAttribute.getThrowableLogAttributes())) {
            final List<ThrowableLogAttribute> throwableLogAttributes = loggingInfo.loggingAttribute.getThrowableLogAttributes();
            for (ThrowableLogAttribute attr : throwableLogAttributes) {
                if (attr.throwable.isInstance(e) && attr.maxRow != 0) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private void log(LoggingInfo loggingInfo, String format, Object... arguments) {
        switch (loggingInfo.loggingAttribute.getLevel()) {
            case INFO:
                loggingInfo.getTargetLog().info(format, arguments);
                break;
            case WARN:
                loggingInfo.getTargetLog().warn(format, arguments);
                break;
            case ERROR:
                loggingInfo.getTargetLog().error(format, arguments);
                break;
            case DEBUG:
                loggingInfo.getTargetLog().debug(format, arguments);
                break;
            case TRACE:
                loggingInfo.getTargetLog().trace(format, arguments);
                break;
        }
    }

    protected LoggingInfo createLoggingIfNecessary(MethodInvocation invocation) {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

        LoggingAttributeSource las = getLoggingAttributeSource();
        final LoggingAttribute loggingAttribute = (las != null ? las.getLoggingAttribute(invocation.getMethod(), targetClass) : null);

        LoggingInfo loggingInfo = new LoggingInfo(loggingAttribute, invocation);
        loggingInfo.bindToThread();
        return loggingInfo;
    }

    protected void cleanupLoggingInfo(@Nullable LoggingInfo loggingInfo) {
        if (loggingInfo != null) {
            loggingInfo.restoreThreadLocalStatus();
        }
    }

    public void setLoggingAttributeSource(@Nullable LoggingAttributeSource loggingAttributeSource) {
        this.loggingAttributeSource = loggingAttributeSource;
    }

    @Nullable
    public LoggingAttributeSource getLoggingAttributeSource() {
        return this.loggingAttributeSource;
    }

    public static LoggingInfo currentLoggingInfo() {
        return loggingInfoHolder.get();
    }

    public static final class LoggingInfo {

        private final LoggingAttribute loggingAttribute;

        private final MethodInvocation invocation;

        private final StopWatch stopWatch;

        private Throwable throwable;

        @Nullable
        private LoggingInfo oldLoggingInfo;

        public LoggingInfo(LoggingAttribute loggingAttribute, MethodInvocation invocation) {
            this.loggingAttribute = loggingAttribute;
            this.invocation = invocation;
            this.stopWatch = new StopWatch();
        }

        public LoggingAttribute getLoggingAttribute() {
            return loggingAttribute;
        }

        public Logger getTargetLog() {
            return loggingAttribute.getTargetLog();
        }

        public int getThrowableLogPrintMaxRow(Throwable e) {
            return loggingAttribute.getThrowableLogPrintMaxRow(e);
        }

        public String getMethodName() {
            return invocation.getMethod().getName();
        }

        public Class<?> getReturnType() {
            return invocation.getMethod().getReturnType();
        }

        public long getTotalTimeMillis() {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            return stopWatch.getTotalTimeMillis();
        }

        private void bindToThread() {
            stopWatch.start();
            this.oldLoggingInfo = loggingInfoHolder.get();
            MDC.put(MDCConstant.CURRENT_METHOD_NAME, getMethodName());
            loggingInfoHolder.set(this);
        }

        private void restoreThreadLocalStatus() {
            loggingInfoHolder.set(this.oldLoggingInfo);
            if (Objects.nonNull(oldLoggingInfo)) {
                MDC.put(MDCConstant.CURRENT_METHOD_NAME, oldLoggingInfo.getMethodName());
            } else {
                MDC.remove(MDCConstant.CURRENT_METHOD_NAME);
            }
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }

    }

}
