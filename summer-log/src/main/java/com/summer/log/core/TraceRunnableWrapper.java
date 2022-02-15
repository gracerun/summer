package com.summer.log.core;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import com.summer.log.constant.LogCategoryConstant;
import com.summer.log.filter.LogCategoryFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 对Runnable接口进行包装,实现多线程环境的日志跟踪。
 * @author Tom
 * @date 1/21/22
 * @version 1.0.0
 */
@Slf4j
public class TraceRunnableWrapper implements Runnable {

    /**
     * Since we don't know the exact operation name we provide a default name for the
     * Span.
     */
    private static final String DEFAULT_SPAN_NAME = "async";

    private final Tracer tracer;

    private final Runnable delegate;

    private final TraceContext parent;

    private final String spanName;

    private final String logCategory;

    public TraceRunnableWrapper(Runnable runnable) {
        this(runnable, null);
    }

    public TraceRunnableWrapper(Runnable runnable, String name) {
        this(runnable, name, null);
    }

    public TraceRunnableWrapper(Runnable runnable, String name, String logCategory) {
        Tracing tracing = TracingHolder.getTracing();
        SpanNamer spanNamer = TracingHolder.getSpanNamer();
        if (Objects.nonNull(tracing)) {
            this.tracer = tracing.tracer();
            this.parent = tracing.currentTraceContext().get();
        } else {
            this.tracer = null;
            this.parent = null;
        }
        this.delegate = runnable;
        if (Objects.nonNull(spanNamer)) {
            this.spanName = name != null ? name : spanNamer.name(delegate, DEFAULT_SPAN_NAME);
        } else {
            this.spanName = name != null ? name : DEFAULT_SPAN_NAME;
        }
        if (StringUtils.hasText(logCategory)) {
            this.logCategory = logCategory;
        } else {
            this.logCategory = LogCategoryConstant.TASK;
        }

    }

    @Override
    public void run() {
        MDC.put(LogCategoryFilter.LOG_CATEGORY_NAME, logCategory);
        ScopedSpan span = null;
        if (Objects.nonNull(this.tracer)) {
            span = this.tracer.startScopedSpanWithParent(this.spanName, this.parent);
        }

        try {
            delegate.run();
        } finally {
            if (Objects.nonNull(span)) {
                span.finish();
            }
            MDC.remove(LogCategoryFilter.LOG_CATEGORY_NAME);
        }
    }

}
