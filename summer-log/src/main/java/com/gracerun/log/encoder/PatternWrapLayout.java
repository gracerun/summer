package com.gracerun.log.encoder;

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
import com.gracerun.log.constant.MDCConstant;
import com.gracerun.log.pattern.DynamicMessageConverter;
import com.gracerun.log.pattern.DynamicThrowableProxyConverter;
import com.gracerun.log.pattern.EnsureDynamicExceptionHandling;
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

    static {
        DEFAULT_CONVERTER_MAP.put("m", DynamicMessageConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("msg", DynamicMessageConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("message", DynamicMessageConverter.class.getName());
        CONVERTER_CLASS_TO_KEY_MAP.put(DynamicMessageConverter.class.getName(), "message");

        DEFAULT_CONVERTER_MAP.put("ex", DynamicThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("exception", DynamicThrowableProxyConverter.class.getName());
        DEFAULT_CONVERTER_MAP.put("throwable", DynamicThrowableProxyConverter.class.getName());
    }

    public PatternWrapLayout() {
        this.postCompileProcessor = new EnsureDynamicExceptionHandling();
    }

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

    public static String formatWrap(StringBuilder strBuilder, StringBuilder logPre) {
        String lineSeparator = System.lineSeparator();
        int lineLength = lineSeparator.length();
        int startIndex;
        if (strBuilder.length() > 0
                && (startIndex = strBuilder.indexOf(lineSeparator)) > 0
                && startIndex < strBuilder.length() - lineLength) {
            StringBuilder strBuilderWrap = new StringBuilder(strBuilder.length() + BIG_STRING_BUILDER_SIZE);
            strBuilderWrap.append(strBuilder.substring(0, startIndex + lineLength));
            startIndex += lineLength;
            int endIndex;
            while ((endIndex = strBuilder.indexOf(lineSeparator, startIndex)) > 0) {
                strBuilderWrap.append(logPre).append(strBuilder.substring(startIndex, endIndex + lineLength));
                startIndex = endIndex + lineLength;
            }
            if (startIndex < strBuilder.length() - 1) {
                strBuilderWrap.append(logPre).append(strBuilder.substring(startIndex));
            }
            return strBuilderWrap.toString();
        }
        return strBuilder.toString();
    }

    @Override
    protected String getPresentationHeaderPrefix() {
        return HEADER_PREFIX;
    }

}
