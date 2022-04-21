
package com.summer.log.pattern;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import com.summer.log.aop.LoggingAspect;

import java.util.Objects;

public class DynamicThrowableProxyConverter extends ThrowableProxyConverter {

    @Override
    protected void subjoinSTEPArray(StringBuilder buf, int indent, IThrowableProxy tp) {
        final LoggingAspect.LoggingInfo loggingInfo = LoggingAspect.currentLoggingInfo();
        if (Objects.nonNull(loggingInfo)) {
            if (tp instanceof ThrowableProxy) {
                final int throwableLogPrintMaxRow = loggingInfo.getThrowableLogPrintMaxRow(((ThrowableProxy) tp).getThrowable());
                if (throwableLogPrintMaxRow == 0) {
                    return;
                } else if (throwableLogPrintMaxRow > 0) {
                    subjoinSTEPArray(buf, indent, tp, throwableLogPrintMaxRow);
                } else {
                    super.subjoinSTEPArray(buf, indent, tp);
                    return;
                }
            }
        }
        super.subjoinSTEPArray(buf, indent, tp);
    }

    protected void subjoinSTEPArray(StringBuilder buf, int indent, IThrowableProxy tp, int lengthOption) {
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        int commonFrames = tp.getCommonFrames();

        boolean unrestrictedPrinting = lengthOption > stepArray.length;

        int maxIndex = (unrestrictedPrinting) ? stepArray.length : lengthOption;
        if (commonFrames > 0 && unrestrictedPrinting) {
            maxIndex -= commonFrames;
        }

        int ignoredCount = 0;
        for (int i = 0; i < maxIndex; i++) {
            StackTraceElementProxy element = stepArray[i];
            if (!isIgnoredStackTraceLine(element.toString())) {
                ThrowableProxyUtil.indent(buf, indent);
                printStackLine(buf, ignoredCount, element);
                ignoredCount = 0;
                buf.append(CoreConstants.LINE_SEPARATOR);
            } else {
                ++ignoredCount;
                if (maxIndex < stepArray.length) {
                    ++maxIndex;
                }
            }
        }
        if (ignoredCount > 0) {
            printIgnoredCount(buf, ignoredCount);
            buf.append(CoreConstants.LINE_SEPARATOR);
        }

        if (commonFrames > 0 && unrestrictedPrinting) {
            ThrowableProxyUtil.indent(buf, indent);
            buf.append("... ").append(tp.getCommonFrames()).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }

    private boolean isIgnoredStackTraceLine(String line) {
        return false;
    }

    private void printStackLine(StringBuilder buf, int ignoredCount, StackTraceElementProxy element) {
        buf.append(element);
        extraData(buf, element); // allow other data to be added
        if (ignoredCount > 0) {
            printIgnoredCount(buf, ignoredCount);
        }
    }

    private void printIgnoredCount(StringBuilder buf, int ignoredCount) {
        buf.append(" [").append(ignoredCount).append(" skipped]");
    }

}
