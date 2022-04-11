
package com.summer.log.core;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.LoggerConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;
import com.summer.log.constant.MDCConstant;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * 日志换行处理
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public class PatternWrapLayout extends PatternLayout {

    public static final int INTIAL_STRING_BUILDER_SIZE = 256;
    public static final int BIG_STRING_BUILDER_SIZE = 1024;
    public static final int LOG_PRE_BUILDER_SIZE = 128;
    public static final String SPACE_STR = " ";

    protected Converter<ILoggingEvent> head;

    @Override
    public void start() {
        if (getPattern() == null || getPattern().length() == 0) {
            addError("Empty or null pattern.");
            return;
        }
        try {
            Parser<ILoggingEvent> p = new Parser<>(getPattern());
            if (getContext() != null) {
                p.setContext(getContext());
            }
            Node t = p.parse();
            this.head = p.compile(t, getEffectiveConverterMap());
            if (postCompileProcessor != null) {
                postCompileProcessor.process(context, head);
            }
            ConverterUtil.setContextForConverters(getContext(), head);
            ConverterUtil.startConverters(this.head);
            started = true;
        } catch (ScanException sce) {
            StatusManager sm = getContext().getStatusManager();
            sm.add(new ErrorStatus("Failed to parse pattern \"" + getPattern() + "\".", this, sce));
        }
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        if (!isStarted()) {
            return CoreConstants.EMPTY_STRING;
        }
        return writeLoopOnConverters(event);
    }

    @Override
    protected String writeLoopOnConverters(ILoggingEvent event) {
        StringBuilder strBuilder = new StringBuilder(INTIAL_STRING_BUILDER_SIZE);
        Converter<ILoggingEvent> c = head;
        StringBuilder logPre = new StringBuilder(LOG_PRE_BUILDER_SIZE);
        while (c != null) {
            c.write(strBuilder, event);
            if (c instanceof LoggerConverter) {
                logPre.append(strBuilder).append(SPACE_STR);
                if (StringUtils.hasText(MDC.get(MDCConstant.CURRENT_METHOD_NAME))) {
                    logPre.append(MDC.get(MDCConstant.CURRENT_METHOD_NAME)).append(SPACE_STR);
                } else {
                    logPre.append("-").append(SPACE_STR);
                }
            }
            c = c.getNext();
        }
        return formatWrap(strBuilder, logPre);

    }

    private String formatWrap(StringBuilder strBuilder, StringBuilder logPre) {
        int index;
        if (strBuilder.length() != 0 && (index = strBuilder.indexOf(CoreConstants.LINE_SEPARATOR)) != -1 && index != strBuilder.length() - 1) {
            StringBuilder newStrBuilder = new StringBuilder(strBuilder.length() + BIG_STRING_BUILDER_SIZE);
            newStrBuilder.append(strBuilder.substring(0, index + 1));
            int lastIndex;
            while ((lastIndex = strBuilder.indexOf(CoreConstants.LINE_SEPARATOR, index + 1)) != -1) {
                newStrBuilder.append(logPre);
                newStrBuilder.append(strBuilder.substring(index + 1, lastIndex + 1));
                index = lastIndex;
            }
            return newStrBuilder.toString();
        }
        return strBuilder.toString();
    }

    @Override
    protected String getPresentationHeaderPrefix() {
        return HEADER_PREFIX;
    }
}
