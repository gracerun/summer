package com.summer.mq.event;

import com.summer.mq.bean.MessageBody;
import org.springframework.context.ApplicationEvent;

/**
 * 消息推送事件
 *
 * @author Tom
 * @date 12/26/21
 * @version 1.0.0
 */
public class MessageEvent extends ApplicationEvent {

    public MessageEvent(MessageBody messageBody) {
        super(messageBody);
    }
}
