
package com.summer.log.pattern;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.summer.log.aop.LoggingAspect;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class DynamicMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        final LoggingAspect.LoggingInfo loggingInfo = LoggingAspect.currentLoggingInfo();
        if (Objects.nonNull(loggingInfo)) {
            final String formattedMessage = event.getFormattedMessage();
            if (StringUtils.hasLength(formattedMessage)
                    && 0 < loggingInfo.getLogging().maxLength()
                    && loggingInfo.getLogging().maxLength() < formattedMessage.length()) {
                return formattedMessage.substring(0, loggingInfo.getLogging().maxLength());
            } else {
                return formattedMessage;
            }
        }
        return event.getFormattedMessage();
    }

}
