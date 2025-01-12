package com.gracerun.component.spring.autoconfigure;

import com.gracerun.component.filter.HttpProxyFilter;
import com.gracerun.component.service.DingTalkReportService;
import com.gracerun.component.service.ReportService;
import com.gracerun.component.service.WechatReportService;
import com.gracerun.component.util.SpringProfilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ComponentAutoConfiguration {

    @Bean
    public SpringProfilesUtil springProfilesUtil() {
        log.info("AutoInit SpringProfilesUtil");
        return new SpringProfilesUtil();
    }

    @Bean
    @ConditionalOnProperty(prefix = "report", name = "send-method", havingValue = "WECHAT")
    @ConditionalOnMissingBean(ReportService.class)
    public ReportService wechatReportService() {
        log.info("AutoInit WechatReportService");
        return new WechatReportService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "report", name = "send-method", havingValue = "DINGDING")
    @ConditionalOnMissingBean(ReportService.class)
    public ReportService dingTalkReportService() {
        log.info("AutoInit DingTalkReportService");
        return new DingTalkReportService();
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "http.proxy.enabled", havingValue = "true")
    public FilterRegistrationBean<HttpProxyFilter> httpTraceFilter() {
        log.info("AutoInit HttpProxyFilter");
        FilterRegistrationBean<HttpProxyFilter> bean = new FilterRegistrationBean<>();
        HttpProxyFilter httpProxyFilter = new HttpProxyFilter();
        bean.setFilter(httpProxyFilter);
        bean.setName(httpProxyFilter.getClass().getSimpleName());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return bean;
    }

}
