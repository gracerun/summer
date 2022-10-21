package com.test.message.web;

import com.summer.mq.bean.MessageBody;
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

    @Autowired
    private MessageInterface messageInterface;

    @PostMapping("/sendAndSave")
    public ResponseEntity sendAndSave(@RequestBody MessageBody messageBody) throws Exception {
        return messageInterface.sendAndSave(messageBody);
    }

    @PostMapping("/log")
    public ResponseEntity log(@RequestBody MessageBody messageBody) {
        final ResponseEntity log = messageInterface.log(messageBody);
        return new ResponseEntity(log.getBody(), log.getStatusCode());
    }

}
