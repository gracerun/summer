package com.gracerun.log.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.brave.bridge.BraveTracer;

import javax.annotation.Resource;
import java.util.Objects;

public class TracerHolder {

    private static BraveTracer braveTracer;

    private static SpanNamer spanNamer;

    private static String instanceIpAddress;

    @Resource
    public void setBraveTracer(BraveTracer braveTracer) {
        TracerHolder.braveTracer = braveTracer;
    }

    @Resource
    public void setSpanNamer(SpanNamer spanNamer) {
        TracerHolder.spanNamer = spanNamer;
    }

    public static Tracer getBraveTracer() {
        return braveTracer;
    }

    public static SpanNamer getSpanNamer() {
        return spanNamer;
    }

    /**
     * 获取SpanID
     * ${INSTANCE_ID},%X{X-B3-SpanId},%X{X-B3-TraceId}
     *
     * @return
     */
    public static String getSanToString() {
        if (Objects.nonNull(braveTracer)) {
            CurrentTraceContext currentTraceContext = braveTracer.currentTraceContext();
            TraceContext traceContext = currentTraceContext.context();
            if (Objects.nonNull(traceContext)) {
                return instanceIpAddress + "," + traceContext.spanId() + "," + traceContext.traceId();
            }
        }
        return null;
    }

    @Value(("${spring.cloud.client.ipAddress:127.0.0.1}"))
    public void setInstanceIpAddress(String instanceIpAddress) {
        TracerHolder.instanceIpAddress = instanceIpAddress;
    }
}
