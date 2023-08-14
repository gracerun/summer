package com.gracerun.message.web;

import com.gracerun.log.annotation.Logging;
import com.gracerun.message.service.OneServiceImpl;
import com.gracerun.message.service.ThreeServiceImpl;
import com.gracerun.message.service.TwoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Tom
 * @version 1.0.0
 * @date 2023/7/27
 */
@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    @Resource
    private OneServiceImpl oneService;

    @Resource
    private TwoServiceImpl twoService;

    @Resource
    private ThreeServiceImpl threeService;

    @PostMapping("/printException1")
    @Logging
    public ResponseEntity printException1() {
        oneService.printException();
        return ResponseEntity.ok("成功");
    }

    @PostMapping("/printException2")
    public ResponseEntity printException2() {
        twoService.printException();
        return ResponseEntity.ok("成功");
    }

}
