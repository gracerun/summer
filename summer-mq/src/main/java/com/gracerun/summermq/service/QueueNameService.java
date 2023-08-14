package com.gracerun.summermq.service;

import com.gracerun.summermq.bean.DelayRule;
import com.gracerun.summermq.constant.QueueConstant;

/**
 * 队列名称
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/29/21
 */
public class QueueNameService {

    public static String fmtTopicQueueName(String namespace, String topic) {
        return namespace + QueueConstant.DELIMITER + topic;
    }

    public static String fmtDelayQueueName(String namespace, int delayLevel) {
        return "{" + namespace + QueueConstant.DELIMITER + QueueConstant.DELAY + "}"
                + DelayRule.fmtLevelName(delayLevel);
    }

    public static String fmtDelayIndexQueueName(String namespace, int delayLevel) {
        return "{" + namespace + QueueConstant.DELIMITER + QueueConstant.DELAY + "}"
                + QueueConstant._INDEX + DelayRule.fmtLevelName(delayLevel);
    }

}
