package com.test.message.web;

import com.summer.log.annotation.Logging;
import com.summer.mq.bean.MessageBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
@Logging
@FeignClient(name = "producer-test", path = "/message")
public interface MessageInterface {

    @PostMapping(value = "/push", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity push(@RequestBody MessageBody messageBody);

    @PostMapping(value = "/log", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity log(@RequestBody MessageBody messageBody);

}
