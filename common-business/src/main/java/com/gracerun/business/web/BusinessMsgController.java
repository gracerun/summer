package com.gracerun.business.web;

import com.gracerun.business.service.BusinessMsg;
import com.gracerun.business.service.BusinessMsgDto;
import com.gracerun.business.service.BusinessMsgQueryParam;
import com.gracerun.business.service.BusinessMsgService;
import com.gracerun.log.annotation.Logging;
import com.gracerun.log.serializer.ToBlankSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 内部消息管理
 *
 * @author Tom
 * @version 1.0.0
 * @date 2024/6/2
 */
@RestController
@Logging
@ConditionalOnProperty(name = "business.msg.path")
@RequestMapping("${business.msg.path}")
public class BusinessMsgController {

    @Resource
    private BusinessMsgService businessMsgService;

    @GetMapping
    @Logging(serializeReturnUsing = ToBlankSerializer.class)
    public ResponseEntity query(BusinessMsgQueryParam query, Pageable pageable) {
        return new ResponseEntity<>(businessMsgService.queryAll(query, pageable), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity create(@Validated @RequestBody BusinessMsgDto resources) {
        return new ResponseEntity<>(businessMsgService.insert(resources), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity update(@Validated @RequestBody BusinessMsgDto resources) {
        businessMsgService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/saveAndExecute")
    public ResponseEntity saveAndExecute(@RequestBody BusinessMsg msg) {
        return new ResponseEntity(businessMsgService.saveAndExecute(msg), HttpStatus.OK);
    }

    @PostMapping("/updateAndExecute")
    public ResponseEntity updateAndExecute(@RequestBody BusinessMsg msg) {
        businessMsgService.updateAndExecute(msg);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/retry")
    public ResponseEntity retry(@RequestBody BusinessMsg msg) {
        businessMsgService.retry(msg);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/batchRetry", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity batchRetry(@RequestBody BusinessMsg resources) {
        return new ResponseEntity(businessMsgService.batchRetry(resources), HttpStatus.OK);
    }
}
