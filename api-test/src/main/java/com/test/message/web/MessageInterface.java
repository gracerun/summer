package com.test.message.web;

import com.gracerun.log.annotation.Logging;
import com.gracerun.log.annotation.ThrowableLog;
import com.gracerun.log.serializer.ToJsonSerializer;
import com.gracerun.summermq.bean.MessageBody;
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
@Logging(throwableLog = {@ThrowableLog(throwable = Throwable.class, maxRow = 5)},
        serializeArgsUsing = ToJsonSerializer.class,
        serializeReturnUsing = ToJsonSerializer.class)
@FeignClient(name = "producer-test", path = "/message", configuration = FooConfiguration.class)
public interface MessageInterface {

    @PostMapping(value = "/sendAndSave", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity sendAndSave(@RequestBody MessageBody messageBody) throws Exception;

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity send(@RequestBody MessageBody messageBody);

    @PostMapping(value = "/log", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity log(@RequestBody MessageBody messageBody);

}
