
package com.gracerun.summermq.consumer;

import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.constant.ConsumeStatus;

public interface MessageListener {

    ConsumeStatus consumeMessage(final MessageBody msg);

}
