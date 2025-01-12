package com.gracerun.component.config;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 基础配置初始化bean
 *
 * @author adc
 * @version 1.0.0
 * @date 9/15/21
 */
@Slf4j
public abstract class BaseConfigInitializingBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            final Map<String, Object> map = BeanUtil.beanToMap(this);
            if (!CollectionUtils.isEmpty(map)) {
                map.forEach((k, v) -> log.info("{}->{}", k, v));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
