package com.summer.mq.constant;

/**
 * 队列常量
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public interface QueueConstant {

    /**
     * 默认消息组
     */
    String DEFAULT_NAMASPACE = "summer.mq.queue";

    /**
     * 分隔符
     */
    String DELIMITER = ":";

    /**
     * 延迟队列
     */
    String DELAY = "delay";

    /**
     * 延迟队列序号
     */
    String DELAY_INDEX = "delay_index";

    String _INDEX = "_index";

    /**
     * 数据加载锁
     */
    String QUEUE_LOAD_LOCK = "load-lock";

    /**
     * 默认重试间隔
     */
    String DELAY_EXPRESSION = "1s 3s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";

    /**
     * 可用处理器数量
     */
    int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();


}
