
package ch.qos.logback.core.pattern;

import ch.qos.logback.classic.pattern.*;
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.color.*;
import ch.qos.logback.core.pattern.parser.Parser;
import com.summer.log.constant.MDCConstant;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志换行处理
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public class PatternWrapLayout extends PatternLayoutBase<ILoggingEvent> {

    static final int BIG_STRING_BUILDER_SIZE = 1024;
    static final int LOG_PRE_BUILDER_SIZE = 128;
    static final String SPACE_STR = " ";

    public static final Map<String, String> defaultConverterMap = new HashMap<String, String>();
    public static final String HEADER_PREFIX = "#logback.classic pattern: ";

    static {
        defaultConverterMap.putAll(Parser.DEFAULT_COMPOSITE_CONVERTER_MAP);

        defaultConverterMap.put("d", DateConverter.class.getName());
        defaultConverterMap.put("date", DateConverter.class.getName());

        defaultConverterMap.put("r", RelativeTimeConverter.class.getName());
        defaultConverterMap.put("relative", RelativeTimeConverter.class.getName());

        defaultConverterMap.put("level", LevelConverter.class.getName());
        defaultConverterMap.put("le", LevelConverter.class.getName());
        defaultConverterMap.put("p", LevelConverter.class.getName());

        defaultConverterMap.put("t", ThreadConverter.class.getName());
        defaultConverterMap.put("thread", ThreadConverter.class.getName());

        defaultConverterMap.put("lo", LoggerConverter.class.getName());
        defaultConverterMap.put("logger", LoggerConverter.class.getName());
        defaultConverterMap.put("c", LoggerConverter.class.getName());

        defaultConverterMap.put("m", MessageConverter.class.getName());
        defaultConverterMap.put("msg", MessageConverter.class.getName());
        defaultConverterMap.put("message", MessageConverter.class.getName());

        defaultConverterMap.put("C", ClassOfCallerConverter.class.getName());
        defaultConverterMap.put("class", ClassOfCallerConverter.class.getName());

        defaultConverterMap.put("M", MethodOfCallerConverter.class.getName());
        defaultConverterMap.put("method", MethodOfCallerConverter.class.getName());

        defaultConverterMap.put("L", LineOfCallerConverter.class.getName());
        defaultConverterMap.put("line", LineOfCallerConverter.class.getName());

        defaultConverterMap.put("F", FileOfCallerConverter.class.getName());
        defaultConverterMap.put("file", FileOfCallerConverter.class.getName());

        defaultConverterMap.put("X", MDCConverter.class.getName());
        defaultConverterMap.put("mdc", MDCConverter.class.getName());

        defaultConverterMap.put("ex", ThrowableProxyConverter.class.getName());
        defaultConverterMap.put("exception", ThrowableProxyConverter.class.getName());
        defaultConverterMap.put("rEx", RootCauseFirstThrowableProxyConverter.class.getName());
        defaultConverterMap.put("rootException", RootCauseFirstThrowableProxyConverter.class.getName());
        defaultConverterMap.put("throwable", ThrowableProxyConverter.class.getName());

        defaultConverterMap.put("xEx", ExtendedThrowableProxyConverter.class.getName());
        defaultConverterMap.put("xException", ExtendedThrowableProxyConverter.class.getName());
        defaultConverterMap.put("xThrowable", ExtendedThrowableProxyConverter.class.getName());

        defaultConverterMap.put("nopex", NopThrowableInformationConverter.class.getName());
        defaultConverterMap.put("nopexception", NopThrowableInformationConverter.class.getName());

        defaultConverterMap.put("cn", ContextNameConverter.class.getName());
        defaultConverterMap.put("contextName", ContextNameConverter.class.getName());

        defaultConverterMap.put("caller", CallerDataConverter.class.getName());

        defaultConverterMap.put("marker", MarkerConverter.class.getName());

        defaultConverterMap.put("property", PropertyConverter.class.getName());

        defaultConverterMap.put("n", LineSeparatorConverter.class.getName());

        defaultConverterMap.put("black", BlackCompositeConverter.class.getName());
        defaultConverterMap.put("red", RedCompositeConverter.class.getName());
        defaultConverterMap.put("green", GreenCompositeConverter.class.getName());
        defaultConverterMap.put("yellow", YellowCompositeConverter.class.getName());
        defaultConverterMap.put("blue", BlueCompositeConverter.class.getName());
        defaultConverterMap.put("magenta", MagentaCompositeConverter.class.getName());
        defaultConverterMap.put("cyan", CyanCompositeConverter.class.getName());
        defaultConverterMap.put("white", WhiteCompositeConverter.class.getName());
        defaultConverterMap.put("gray", GrayCompositeConverter.class.getName());
        defaultConverterMap.put("boldRed", BoldRedCompositeConverter.class.getName());
        defaultConverterMap.put("boldGreen", BoldGreenCompositeConverter.class.getName());
        defaultConverterMap.put("boldYellow", BoldYellowCompositeConverter.class.getName());
        defaultConverterMap.put("boldBlue", BoldBlueCompositeConverter.class.getName());
        defaultConverterMap.put("boldMagenta", BoldMagentaCompositeConverter.class.getName());
        defaultConverterMap.put("boldCyan", BoldCyanCompositeConverter.class.getName());
        defaultConverterMap.put("boldWhite", BoldWhiteCompositeConverter.class.getName());
        defaultConverterMap.put("highlight", HighlightingCompositeConverter.class.getName());

        defaultConverterMap.put("lsn", LocalSequenceNumberConverter.class.getName());

    }

    public PatternWrapLayout() {
        this.postCompileProcessor = new EnsureExceptionHandling();
    }

    @Override
    public Map<String, String> getDefaultConverterMap() {
        return defaultConverterMap;
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

    //处理日志换行问题
    private String formatWrap(StringBuilder strBuilder, StringBuilder logPre) {
        int index = -1;
        if (strBuilder.length() != 0 && (index = strBuilder.indexOf(CoreConstants.LINE_SEPARATOR)) != -1 && index != strBuilder.length() - 1) {
            StringBuilder newStrBuilder = new StringBuilder(strBuilder.length() + BIG_STRING_BUILDER_SIZE);
            newStrBuilder.append(strBuilder.substring(0, index + 1));
            int lastIndex = -1;
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
