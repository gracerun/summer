package cn.happyselect.sys.config;

import cn.happyselect.sys.filter.OperLogFilter;
import cn.happyselect.sys.service.OperLogServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 系统配置
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-13
 */
@Configuration
public class SysConfig {

    @Value("${sys.operlog.exclusions:}")
    private String operLogExclusions;

    @Bean
    public FilterRegistrationBean operLogFilter(OperLogServiceImpl operLogService) {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        OperLogFilter operLogFilter = new OperLogFilter(operLogService);
        operLogFilter.setExclusions(operLogExclusions);
        bean.setFilter(operLogFilter);
        bean.setName(operLogFilter.getClass().getSimpleName());
        bean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER + 100);
        return bean;
    }

}
