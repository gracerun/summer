package com.gracerun.log.core;

import com.gracerun.log.constant.LogCategoryConstant;
import com.gracerun.log.filter.LogCategoryFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 对Supplier接口进行包装,实现多线程环境的日志文件按类型分割。
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/8/10
 */
@Slf4j
public class TraceSupplierWrapper<T> implements Supplier<T> {

    private final String logCategory;

    private final Supplier<T> supplier;

    public TraceSupplierWrapper(Supplier<T> supplier) {
        this(supplier, false);
    }

    public TraceSupplierWrapper(Supplier<T> supplier, boolean continueSpan) {
        this(supplier, continueSpan, null);
    }

    public TraceSupplierWrapper(Supplier<T> supplier, boolean continueSpan, String logCategory) {
        this(supplier, continueSpan, logCategory, null);
    }

    public TraceSupplierWrapper(Supplier<T> supplier, boolean continueSpan, String logCategory, String name) {
        if (StringUtils.hasText(logCategory)) {
            this.logCategory = logCategory;
        } else {
            this.logCategory = LogCategoryConstant.ASYNC;
        }
        if (Objects.nonNull(TracerHolder.getTracer())) {
            if (continueSpan) {
                this.supplier = new TraceSupplier(TracerHolder.getTracer(), TracerHolder.getSpanNamer(), supplier, name);
            } else {
                this.supplier = new TraceSupplier(TracerHolder.getTracer(), null, TracerHolder.getSpanNamer(), supplier, name);
            }
        } else {
            this.supplier = supplier;
        }
    }

    @Override
    public T get() {
        MDC.put(LogCategoryFilter.LOG_CATEGORY_NAME, logCategory);
        try {
            return this.supplier.get();
        } finally {
            MDC.remove(LogCategoryFilter.LOG_CATEGORY_NAME);
        }
    }

    private class TraceSupplier<T> implements Supplier<T> {

        private static final String DEFAULT_SPAN_NAME = "async";

        private final Tracer tracer;

        private final Supplier<T> supplier;

        private final Span parent;

        private final String spanName;

        public TraceSupplier(Tracer tracer, SpanNamer spanNamer, Supplier<T> supplier, String name) {
            this(tracer, tracer.currentSpan(), spanNamer, supplier, name);
        }

        public TraceSupplier(Tracer tracer, Span parent, SpanNamer spanNamer, Supplier<T> supplier, String name) {
            this.tracer = tracer;
            this.supplier = supplier;
            this.parent = parent;
            this.spanName = name != null ? name : spanNamer.name(supplier, DEFAULT_SPAN_NAME);
        }

        @Override
        public T get() {
            Span childSpan = this.tracer.nextSpan(this.parent).name(this.spanName);
            try (Tracer.SpanInScope ws = this.tracer.withSpan(childSpan.start())) {
                return this.supplier.get();
            } catch (Exception | Error e) {
                childSpan.error(e);
                throw e;
            } finally {
                childSpan.end();
            }
        }

    }

}
