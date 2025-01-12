package com.gracerun.business.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Slf4j
@ComponentScan({"com.gracerun.business.web", "com.gracerun.business.service"})
@MapperScan(basePackages = "com.gracerun.business.mapper")
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class BusinessAutoConfiguration {

}
