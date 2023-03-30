package com.gracerun.util.spring.autoconfigure;

import com.gracerun.util.SpringProfilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class UtilAutoConfiguration {

    @Bean
    public SpringProfilesUtil springProfilesUtil() {
        log.info("Init SpringProfilesUtil");
        return new SpringProfilesUtil();
    }

}
