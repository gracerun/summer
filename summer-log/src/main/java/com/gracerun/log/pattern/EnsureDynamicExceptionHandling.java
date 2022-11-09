
package com.gracerun.log.pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PostCompileProcessor;

public class EnsureDynamicExceptionHandling implements PostCompileProcessor<ILoggingEvent> {

    @Override
    public void process(Context context, Converter<ILoggingEvent> head) {
        if (head == null) {
            // this should never happen
            throw new IllegalArgumentException("cannot process empty chain");
        }
        if (!chainHandlesThrowable(head)) {
            Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
            Converter<ILoggingEvent> exConverter = null;
            LoggerContext loggerContext = (LoggerContext) context;
            if (loggerContext.isPackagingDataEnabled()) {
                exConverter = new ExtendedThrowableProxyConverter();
            } else {
                // 设置默认的异常转换器
                exConverter = new DynamicThrowableProxyConverter();
            }
            tail.setNext(exConverter);
        }
    }

    /**
     * This method computes whether a chain of converters handles exceptions or
     * not.
     *
     * @param head The first element of the chain
     * @return true if can handle throwables contained in logging events
     */
    public boolean chainHandlesThrowable(Converter<ILoggingEvent> head) {
        Converter<ILoggingEvent> c = head;
        while (c != null) {
            if (c instanceof ThrowableHandlingConverter) {
                return true;
            }
            c = c.getNext();
        }
        return false;
    }
}
