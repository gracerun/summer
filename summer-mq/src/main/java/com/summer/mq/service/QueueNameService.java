package com.summer.mq.service;

import com.summer.mq.bean.DelayRule;
import com.summer.mq.constant.QueueConstant;

/**
 * 队列名称
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/29/21
 */
public class QueueNameService {

    public static String fmtTopicQueueName(String group, String topic) {
        return group + QueueConstant.DELIMITER + topic;
    }

    public static String fmtDelayQueueName(String group, int delayLevel) {
        return group + QueueConstant.DELIMITER + QueueConstant.DELAY + DelayRule.fmtLevelName(delayLevel);
    }

    public static String fmtDelayIndexQueueName(String group, int delayLevel) {
        return group + QueueConstant.DELIMITER + QueueConstant.DELAY_INDEX + DelayRule.fmtLevelName(delayLevel);
    }

}
