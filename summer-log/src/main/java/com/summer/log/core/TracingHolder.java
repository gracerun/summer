package com.summer.log.core;

import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.SpanNamer;

import java.util.Objects;

public class TracingHolder {

    private static Tracing tracing;

    private static SpanNamer spanNamer;

    private static String instanceIpAddress;

    @Autowired(required = false)
    public void setTracing(Tracing tracing) {
        TracingHolder.tracing = tracing;
    }

    @Autowired(required = false)
    public void setSpanNamer(SpanNamer spanNamer) {
        TracingHolder.spanNamer = spanNamer;
    }

    public static Tracing getTracing() {
        return tracing;
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
        if (Objects.nonNull(tracing)) {
            CurrentTraceContext currentTraceContext = tracing.currentTraceContext();
            TraceContext traceContext = currentTraceContext.get();
            if (Objects.nonNull(traceContext)) {
                return instanceIpAddress + "," + traceContext.spanIdString() + "," + traceContext.traceIdString();
            }
        }
        return null;
    }

    @Value(("${spring.cloud.client.ipAddress:127.0.0.1}"))
    public void setInstanceIpAddress(String instanceIpAddress) {
        TracingHolder.instanceIpAddress = instanceIpAddress;
    }
}
