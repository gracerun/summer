package com.gracerun.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.StringJoiner;

@Slf4j
public class SpringProfilesUtil {
    /**
     * 开发
     */
    public static final String DEV = "dev";

    /**
     * 测试
     */
    public static final String TEST = "test";

    /**
     * 灰度
     */
    public static final String GRAY = "gray";

    /**
     * 生产
     */
    public static final String PROD = "prod";

    private static SpringProfilesUtil CONFIG;

    @PostConstruct
    private void init() {
        CONFIG = this;
        log.info("{}", this);
    }

    @Value("${spring.profiles.active:}")
    private String active;

    public static String getActive() {
        return CONFIG.active;
    }

    public static boolean isProd() {
        return Objects.nonNull(CONFIG) && StringUtils.hasText(CONFIG.active) && CONFIG.active.contains(PROD);
    }

    public static boolean isDev() {
        return Objects.nonNull(CONFIG) && StringUtils.hasText(CONFIG.active) && CONFIG.active.contains(DEV);
    }

    public static boolean isTest() {
        return Objects.nonNull(CONFIG) && StringUtils.hasText(CONFIG.active) && CONFIG.active.contains(TEST);
    }

    public static boolean isGray() {
        return Objects.nonNull(CONFIG) && StringUtils.hasText(CONFIG.active) && CONFIG.active.contains(GRAY);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpringProfilesUtil.class.getSimpleName() + "[", "]")
                .add("active='" + active + "'")
                .toString();
    }
}
