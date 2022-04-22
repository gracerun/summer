package com.test.message.web;

import com.summer.log.annotation.Logging;
import com.summer.log.annotation.ThrowableLog;
import com.summer.mq.bean.MessageBody;
import com.summer.mq.producer.SummerMQTemplate;
import com.test.message.service.BaseService;
import lombok.extern.slf4j.Slf4j;
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
@Logging(throwableLog = {@ThrowableLog(throwable = Throwable.class, maxRow = 5)})
@RestController
@RequestMapping("/message")
public class MessageController implements MessageInterface {

    @Autowired
    private SummerMQTemplate summerMQTemplate;

    @Autowired
    private BaseService baseService;

    @Override
    @PostMapping("/push")
    public ResponseEntity push(@RequestBody MessageBody messageBody) {
        summerMQTemplate.sendAndSave(messageBody);
        return ResponseEntity.ok("发送成功");
    }

    @Override
    @PostMapping("/log")
    @Transactional
    public ResponseEntity log(@RequestBody MessageBody messageBody) {
        try {
            baseService.print();
        } catch (Exception e) {
            log.error("控制层报错了{}-{}", e.getMessage(), e);
        }
        return ResponseEntity.ok("发送成功");
    }

}
