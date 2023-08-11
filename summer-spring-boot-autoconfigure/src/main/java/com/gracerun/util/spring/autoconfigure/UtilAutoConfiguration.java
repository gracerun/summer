package com.gracerun.util.spring.autoconfigure;

import com.gracerun.util.SpringProfilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Slf4j
@ConditionalOnClass({SpringProfilesUtil.class})
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class UtilAutoConfiguration {

    @Bean
    public SpringProfilesUtil springProfilesUtil() {
        log.info("Init SpringProfilesUtil");
        return new SpringProfilesUtil();
    }

}
