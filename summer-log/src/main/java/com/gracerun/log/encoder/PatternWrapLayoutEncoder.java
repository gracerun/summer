package com.gracerun.log.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
        Charset charset = this.getCharset();
        if (Objects.isNull(charset)) {
            this.setCharset(StandardCharsets.UTF_8);
        }
        super.start();
    }

}
