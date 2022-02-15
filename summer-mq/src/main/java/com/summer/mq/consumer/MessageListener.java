
package com.summer.mq.consumer;

import com.summer.mq.constant.ConsumeStatus;
import com.summer.mq.bean.MessageBody;

public interface MessageListener {

    ConsumeStatus consumeMessage(final MessageBody msg);

}
