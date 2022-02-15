package com.summer.log.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.summer.log.constant.LogCategoryConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 日志过滤器
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public class LogCategoryFilter extends Filter<ILoggingEvent> {

    public static final String LOG_CATEGORY_NAME = "X-log-category";

    @Getter
    @Setter
    private String value;

    private static final String defaultValue = LogCategoryConstant.DEFAULT;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        String logCategoryValue = getLogCategoryValue(event);
        if (logCategoryValue.equals(value)) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    public String getLogCategoryValue(ILoggingEvent event) {
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (mdcMap == null) {
            return defaultValue;
        }
        String mdcValue = mdcMap.get(LOG_CATEGORY_NAME);
        if (mdcValue == null) {
            return defaultValue;
        } else {
            return mdcValue;
        }
    }

}
