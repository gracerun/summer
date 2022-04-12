package com.test.message.web;

import com.summer.mq.bean.MessageBody;
import com.summer.mq.producer.SummerMQTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tom
 * @version 1.0.0
 * @date 1/17/22
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired(required = false)
    private SummerMQTemplate summerMQTemplate;

    @PostMapping("/push")
    public ResponseEntity push(@RequestBody MessageBody messageBody) {
        log.info("{}", messageBody);
        summerMQTemplate.sendAndSave(messageBody);
        return ResponseEntity.ok("发送成功");
    }

}
