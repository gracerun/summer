package com.gracerun.summermq.event;

import com.gracerun.summermq.bean.MessageBody;
import org.springframework.context.ApplicationEvent;

/**
 * 消息推送事件
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public class MessageEvent extends ApplicationEvent {

    public MessageEvent(MessageBody messageBody) {
        super(messageBody);
    }
}
