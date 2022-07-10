package com.test.message.web;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {

    @org.junit.jupiter.api.Test
    public void countTest() {
        for (int i = 0; i <= 12; i++) {
            log.info(i + "月总数:{}", make(0, 1, i));
        }

    }

    private int make(int age, int startTime, int endTime) {
        if (startTime > endTime) {
            return 0;
        }
        if (age >= 2) {
            int total = 1 + make(0, startTime, endTime)
                    + make(age + 1, startTime + 1, endTime);
            return total;
        } else {
            return make(age + 1, startTime + 1, endTime);
        }
    }

}