package com.gracerun.util;

import com.gracerun.summermq.bean.MessageBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageConsumerTest implements Runnable {

    private MessageBody message;

    public MessageConsumerTest(MessageBody message) {
        this.message = message;
    }

    @Override
    public void run() {
        log.info("消费开始{}", message.getBusinessNo());
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("消费结束{}", message.getBusinessNo());
    }


}