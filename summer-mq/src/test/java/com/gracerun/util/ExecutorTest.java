package com.gracerun.util;


import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.service.ExecutorUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class ExecutorTest {

    @Test
    public void test1() throws InterruptedException {
        log.info("{}", Thread.currentThread().getName());
        ThreadPoolExecutor monitorThread = ExecutorUtil.createExecutor("monitor-",
                1, 1, 0, 1, new ThreadPoolExecutor.CallerRunsPolicy());

        ThreadPoolExecutor consumerThread = ExecutorUtil.createExecutor("consumer-",
                1, 5, 10, 10, new ThreadPoolExecutor.CallerRunsPolicy());


        monitorThread.submit(() -> {
            while (true) {
                log.info("{}-{}-{}-{}-{}-{}-{}", consumerThread.getActiveCount(), consumerThread.getTaskCount(),
                        consumerThread.getCompletedTaskCount(), consumerThread.getCorePoolSize(),
                        consumerThread.getLargestPoolSize(), consumerThread.getMaximumPoolSize(),
                        consumerThread.getQueue().size());
                Thread.sleep(1000);
            }
        });

        for (int i = 0; i < 20; i++) {
            log.info("生产{}", i);
            int cachedMessageCount = consumerThread.getQueue().size();
            if (cachedMessageCount >= 10) {
                log.warn("the cached message count exceeds the threshold {}, so do flow control, count={}, pullRequest={}, flowControlTimes={}");
            }
            consumerThread.submit(new MessageConsumerTest(new MessageBody().setBusinessNo(i + "")));
            Thread.sleep(500);
        }

        Thread.sleep(22 * 1000);

        for (int i = 20; i < 40; i++) {
            log.info("生产{}", i);
            int cachedMessageCount = consumerThread.getQueue().size();
            if (cachedMessageCount >= 10) {
                log.warn("the cached message count exceeds the threshold {}, so do flow control, count={}, pullRequest={}, flowControlTimes={}");
            }
            consumerThread.submit(new MessageConsumerTest(new MessageBody().setBusinessNo(i + "")));
            Thread.sleep(500);
        }

        Thread.sleep(100 * 1000);
    }
}
