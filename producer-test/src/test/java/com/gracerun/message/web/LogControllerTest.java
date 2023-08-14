package com.gracerun.message.web;

import ch.qos.logback.classic.Level;
import com.gracerun.util.HttpBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class LogControllerTest {

    protected static final RequestConfig config = RequestConfig
            .custom()
            .setSocketTimeout(10000)
            .setConnectTimeout(10000)
            .setConnectionRequestTimeout(2000)
            .build();

    /**
     * @throws IOException
     * @see LogController#printException1()
     */
    @Test
    public void printException1() throws IOException {
        HttpBuilder.post("http://localhost:8008/log/printException1")
                .setConfig(config)
                .setLevel(Level.INFO)
                .execute();
    }

    /**
     * @throws IOException
     * @see LogController#printException2()
     */
    @Test
    public void printException2() throws IOException {
        HttpBuilder.post("http://localhost:8008/log/printException2")
                .setConfig(config)
                .setLevel(Level.INFO)
                .execute();
    }

}