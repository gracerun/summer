package com.gracerun.log.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;

import java.util.Objects;

public class TracerHolder {

    private static Tracer tracer;

    private static SpanNamer spanNamer;

    private static String instanceIpAddress;

    @Autowired
    public void setTracer(Tracer tracer) {
        TracerHolder.tracer = tracer;
    }

    @Autowired
    public void setSpanNamer(SpanNamer spanNamer) {
        TracerHolder.spanNamer = spanNamer;
    }

    public static Tracer getTracer() {
        return tracer;
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
        if (Objects.nonNull(tracer)) {
            CurrentTraceContext currentTraceContext = tracer.currentTraceContext();
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
