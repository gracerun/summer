package com.gracerun.message.service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Tom
 * @version 1.0.0
 * @date 2023/7/31
 */
public class AtomicIntegerTest {
    private static final AtomicInteger seq = new AtomicInteger(0);

    public static int increment() {
        return seq.incrementAndGet();
    }
}
