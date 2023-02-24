package com.test.message.web;

import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.producer.SummerMQTemplate;
import com.test.message.service.AppleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
public class MessageController implements MessageInterface {

    @Autowired
    private SummerMQTemplate summerMQTemplate;

    @Autowired
    private AppleServiceImpl appleService;

    @Override
    @PostMapping("/sendAndSave")
    @Transactional
    public ResponseEntity sendAndSave(@RequestBody MessageBody messageBody) {
        summerMQTemplate.sendAndSave(messageBody);
        return ResponseEntity.ok("发送成功");
    }

    @PostMapping("/delaySend")
    @Transactional
    public ResponseEntity delaySend(@RequestBody MessageBody messageBody) {
        messageBody.setNextExecuteTime(new DateTime().plusMinutes(1).toDate());
        summerMQTemplate.sendAndSave(messageBody);
        return ResponseEntity.ok("发送成功");
    }

    @Override
    @PostMapping("/send")
    public ResponseEntity send(@RequestBody MessageBody messageBody) {
        summerMQTemplate.send(messageBody);
        return ResponseEntity.ok("发送成功");
    }

    @Override
    @PostMapping("/log")
    public ResponseEntity log(@RequestBody MessageBody messageBody) {
        try {
            appleService.printName();
        } catch (Exception e) {
            log.error("控制层报错了{}-{}", e.getMessage(), e);
        }
        return ResponseEntity.ok("发送成功");
    }

}
