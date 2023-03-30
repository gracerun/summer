package com.gracerun.log.core;

import brave.baggage.BaggageFields;
import brave.propagation.TraceContext;
import com.gracerun.log.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppContextLogInitializer implements ApplicationContextInitializer {

    public static final String SPRING_LOCALHOST_IP_ADDRESS = "spring.localhost.ip-address";
    private static final Random RANDOM = new Random();
    private static final String LOCALHOST_IP = IpUtil.getIp();

    static {
        if (StringUtils.hasText(LOCALHOST_IP)) {
            int index = LOCALHOST_IP.lastIndexOf(".");
            if (index > -1) {
                index = LOCALHOST_IP.lastIndexOf(".", index - 1);
            }
            if (index > -1) {
                System.setProperty(SPRING_LOCALHOST_IP_ADDRESS, LOCALHOST_IP.substring(index + 1));
            } else {
                System.setProperty(SPRING_LOCALHOST_IP_ADDRESS, LOCALHOST_IP);
            }
        }

        System.setProperty(LoggingSystem.SYSTEM_PROPERTY, LogbackCategoryLoggingSystem.class.getName());
        initSpan();
    }

    private static void initSpan() {
        long id = createId();
        final TraceContext build = TraceContext.newBuilder().spanId(id).traceId(id).traceIdHigh(0).build();
        logStartedSpan(build);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info(LOCALHOST_IP);
        try {
            printSystemProperties();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void printSystemProperties() {
        final Properties properties = System.getProperties();
        final Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            final Object o = enumeration.nextElement();
            final String property = properties.getProperty((String) o);
            log.info("{}: {}", o, property);
        }
    }

    public static void logStartedSpan(TraceContext traceContext) {
        MDC.put(BaggageFields.SPAN_ID.name(), traceContext.spanIdString());
        MDC.put(BaggageFields.TRACE_ID.name(), traceContext.traceIdString());
    }

    private static long createId() {
        return RANDOM.nextLong();
    }
}