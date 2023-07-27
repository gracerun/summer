package com.gracerun.message.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppleServiceImpl extends FruitService implements FruitInterface {

    @Override
    public void printName() {
        super.printName();
        log.info("appleInner: My name is Apple");
        log.error("This class printInnerException maxRow is 1", new RuntimeException("This is RunTimeException"));
        log.error("This class printInnerException maxRow is 5", new IllegalArgumentException("This is IllegalArgumentException"));
        log.error("This class printInnerException maxRow is 3", new NullPointerException("This is NullPointerException"));
    }
}
