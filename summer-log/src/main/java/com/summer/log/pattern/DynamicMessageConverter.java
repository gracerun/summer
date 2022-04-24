
package com.summer.log.pattern;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.summer.log.interceptor.LoggingInterceptor;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class DynamicMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        final LoggingInterceptor.LoggingInfo loggingInfo = LoggingInterceptor.currentLoggingInfo();
        if (Objects.nonNull(loggingInfo)) {
            final String formattedMessage = event.getFormattedMessage();
            final int maxLength = loggingInfo.getLoggingAttribute().getMaxLength();
            if (StringUtils.hasText(formattedMessage)
                    && 0 < maxLength
                    && maxLength < formattedMessage.length()) {
                return formattedMessage.substring(0, maxLength);
            } else {
                return formattedMessage;
            }
        }
        return event.getFormattedMessage();
    }

}
