
package com.gracerun.summermq.consumer;

import com.gracerun.summermq.constant.ConsumeStatus;
import com.gracerun.summermq.bean.MessageBody;

public interface MessageListener {

    ConsumeStatus consumeMessage(final MessageBody msg);

}
