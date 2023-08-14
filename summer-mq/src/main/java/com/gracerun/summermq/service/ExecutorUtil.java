package com.gracerun.summermq.service;

import com.gracerun.summermq.bean.DelayRule;
import com.gracerun.summermq.constant.QueueConstant;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/29/21
 */
public class ExecutorUtil {

    public static ThreadPoolExecutor createExecutor(String threadNamePrefix,
                                                    int corePoolSize,
                                                    int maximumPoolSize,
                                                    long keepAliveSeconds,
                                                    int queueCapacity,
                                                    RejectedExecutionHandler handler) {
        final CustomizableThreadFactory customizableThreadFactory = new CustomizableThreadFactory();
        customizableThreadFactory.setThreadNamePrefix(threadNamePrefix);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveSeconds, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                customizableThreadFactory,
                handler);
    }

    public static String fmtThreadNamePrefix(String namespace) {
        return namespace + QueueConstant.DELIMITER + QueueConstant.DELAY + "-";
    }

    public static String fmtThreadNamePrefix(String namespace, int delayLevel) {
        return namespace + QueueConstant.DELIMITER + QueueConstant.DELAY + DelayRule.fmtLevelName(delayLevel) + "-";
    }

}
