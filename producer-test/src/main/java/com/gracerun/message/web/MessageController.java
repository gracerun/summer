package com.gracerun.message.web;

import cn.hutool.core.bean.BeanUtil;
import com.gracerun.message.bean.GraceMessage;
import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.producer.SummerMQTemplate;
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
@RestController
@RequestMapping("/message")
public class MessageController implements MessageInterface {

    @Autowired
    private SummerMQTemplate summerMQTemplate;

    @Override
    @PostMapping("/sendAndSave")
    @Transactional
    public ResponseEntity sendAndSave(@RequestBody GraceMessage message) {
        summerMQTemplate.sendAndSave(BeanUtil.toBean(message, MessageBody.class));
        return ResponseEntity.ok("发送成功");
    }

    @PostMapping("/delaySend")
    @Transactional
    public ResponseEntity delaySend(@RequestBody GraceMessage message) {
        summerMQTemplate.sendAndSave(BeanUtil.toBean(message, MessageBody.class));
        return ResponseEntity.ok("发送成功");
    }

    @Override
    @PostMapping("/send")
    public ResponseEntity send(@RequestBody GraceMessage message) {
        summerMQTemplate.send(BeanUtil.toBean(message, MessageBody.class));
        return ResponseEntity.ok("发送成功");
    }

}
