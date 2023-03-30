package com.gracerun.summermq.event;

import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.producer.RedisMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 消息监听
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public class MessageEventListener {

    @Autowired
    private RedisMessageProducer redisMessageProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void afterCommit(MessageEvent event) {
        final MessageBody source = (MessageBody) event.getSource();
        redisMessageProducer.asyncSend(source);
    }
}
