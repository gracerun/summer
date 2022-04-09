package com.summer.log.core;

import com.summer.log.constant.LogCategoryConstant;
import com.summer.log.filter.LogCategoryFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.instrument.async.TraceRunnable;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 对Runnable接口进行包装,实现多线程环境的日志文件按类型分割。
 *
 * @author Tom
 * @version 1.0.0
 * @date 1/21/22
 */
@Slf4j
public class TraceRunnableWrapper implements Runnable {

    private final String logCategory;

    private final Runnable delegate;

    public TraceRunnableWrapper(Runnable delegate) {
        this(delegate, null);
    }

    public TraceRunnableWrapper(Runnable delegate, String name) {
        this(delegate, name, null);
    }

    public TraceRunnableWrapper(Runnable delegate, String name, String logCategory) {
        if (StringUtils.hasText(logCategory)) {
            this.logCategory = logCategory;
        } else {
            this.logCategory = LogCategoryConstant.TASK;
        }
        if (Objects.nonNull(TracerHolder.getTracer())) {
            this.delegate = new TraceRunnable(TracerHolder.getTracer(), TracerHolder.getSpanNamer(), delegate, name);
        } else {
            this.delegate = delegate;
        }
    }

    @Override
    public void run() {
        MDC.put(LogCategoryFilter.LOG_CATEGORY_NAME, logCategory);
        try {
            delegate.run();
        } finally {
            MDC.remove(LogCategoryFilter.LOG_CATEGORY_NAME);
        }
    }

}
