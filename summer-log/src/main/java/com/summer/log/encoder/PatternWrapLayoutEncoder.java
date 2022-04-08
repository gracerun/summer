package com.summer.log.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import ch.qos.logback.core.pattern.PatternWrapLayout;

/**
 * 扩展PatternLayoutEncoderWrap
 * 解决日志换行导致TraceId丢失的问题
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public class PatternWrapLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

    @Override
    public void start() {
        PatternWrapLayout patternLayout = new PatternWrapLayout();
        patternLayout.setContext(this.context);
        patternLayout.setPattern(this.getPattern());
        patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

}
