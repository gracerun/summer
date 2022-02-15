//package com.test.message.consumer;
//
//import com.summer.mq.annotation.SummerMQMessageListener;
//import com.summer.mq.bean.MessageBody;
//import com.summer.mq.constant.ConsumeStatus;
//import com.summer.mq.consumer.MessageListener;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * 消息通知
// *
// * @author adc
// * @version 1.0.0
// * @date 6/9/21
// */
//@Slf4j
//@Component
//@SummerMQMessageListener(topic = "pay_notify", repeatRetry = true, delayExpression = "6s 12s")
//public class PayNotifyConsumer implements MessageListener {
//
//    @Override
//    public ConsumeStatus consumeMessage(MessageBody msg) {
//        log.info("msgId:{}", msg.getId());
//        try {
//            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(10, 20));
//        } catch (InterruptedException e) {
//            log.error(e.getMessage(), e);
//        }
//        return ConsumeStatus.CONSUME_SUCCESS;
//
//    }
//}
