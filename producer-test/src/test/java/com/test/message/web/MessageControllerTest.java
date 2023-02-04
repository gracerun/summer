package com.test.message.web;

import ch.qos.logback.classic.Level;
import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.util.HttpBuilder;
import io.lettuce.core.RedisAsyncCommandsImpl;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MessageControllerTest {
    protected static final RequestConfig config = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(2000).build();

    public static void main(String[] args) {
        final RuntimeException e = new RuntimeException();
        log.error(e.getMessage(), e);

//        e.printStackTrace();
    }

    @Test
    public void exeTest() {

    }

    @Test
    public void testLog() throws IOException {
        final MessageBody messageBody = new MessageBody();
        messageBody.setBusinessNo(System.currentTimeMillis() + "");
        messageBody.setBusinessType("msg_notify");
        messageBody.setContent("测试消息");
        HttpBuilder.post("http://localhost:8008/message/log").setConfig(config).setLevel(Level.INFO).setJsonParam(messageBody).execute();
//        HttpBuilder.post("http://localhost:8009/message/log").setConfig(config).setLevel(Level.INFO).setJsonParam(messageBody).execute();
    }

    @Test
    public void send() {
        for (int i = 0; i < 1; i++) {
            try {
                final MessageBody messageBody = new MessageBody();
                messageBody.setBusinessNo(System.currentTimeMillis() + "");
                messageBody.setBusinessType("msg_notify");
                messageBody.setContent(i + "测试消息");
                HttpBuilder.post("http://localhost:8008/message/send").setConfig(config).setLevel(Level.INFO).setJsonParam(messageBody).execute();
//                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Test
    public void sendAndSave() {
        for (int i = 0; i < 1; i++) {
            try {
                final MessageBody messageBody = new MessageBody();
                messageBody.setBusinessNo(System.currentTimeMillis() + "");
                messageBody.setBusinessType("msg_notify");
                messageBody.setContent(i + "测试消息");
                HttpBuilder.post("http://localhost:8008/message/sendAndSave").setConfig(config).setLevel(Level.INFO).setJsonParam(messageBody).execute();
//                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Test
    public void pushMessage2() throws IOException, InterruptedException {
        for (int i = 0; i < 100; i++) {
            final MessageBody messageBody = new MessageBody();
            messageBody.setBusinessNo(System.currentTimeMillis() + "");
            messageBody.setBusinessType("msg_notify");
            messageBody.setContent(i + "");
            HttpBuilder.post("http://localhost:8008/message/push").setConfig(config).setLevel(Level.INFO).setJsonParam(messageBody).execute();
//            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void test3() {

        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());

        final CountDownLatch countDownLatch = new CountDownLatch(1000);

        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(new EExecute(countDownLatch));
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPoolExecutor.shutdown();
        System.out.println("发送完成");
    }

    static class EExecute implements Runnable {
        CountDownLatch countDownLatch;

        public EExecute(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
//            pushMessage();
            countDownLatch.countDown();
        }

    }

    @Test
    public void testLambda() {
        funciontTest("123123", RedisClusterAsyncCommands::brpop, 10, new byte[]{});
    }

    public <R, T1, T2> RedisFuture<R> funciontTest(String s, ConnectionFunction2<T1, T2, R> function, T1 t1, T2 t2) {
        return function.apply(new RedisAsyncCommandsImpl(null, null), t1, t2);
    }

    @FunctionalInterface
    interface ConnectionFunction2<T1, T2, R> {

        RedisFuture<R> apply(RedisClusterAsyncCommands<byte[], byte[]> connection, T1 t1, T2 t2);
    }

}