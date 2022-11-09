package com.gracerun.summermq.producer;

import com.gracerun.summermq.annotation.PushMessage;
import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.service.MessagePersistentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class SummerMQTemplate {

    @Autowired(required = false)
    public MessagePersistentService messagePersistentService;

    @Autowired
    public RedisMessageProducer redisMessageProducer;

    @PushMessage
    public void sendAndSave(MessageBody messageBody) {
        if (Objects.nonNull(messagePersistentService)) {
            messageBody.setPersistent(true);
            messagePersistentService.save(messageBody);
        }
    }

    @PushMessage
    public void send(MessageBody messageBody) {
        messageBody.setPersistent(false);
    }

}
