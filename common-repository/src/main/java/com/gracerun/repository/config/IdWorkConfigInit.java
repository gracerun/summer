package com.gracerun.repository.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;

/**
 * 修改IdWorker默认生成规则
 *
 * @author Tom
 * @version 1.0.0
 * @date 2024/9/5
 */
@Slf4j
public class IdWorkConfigInit implements InitializingBean {

    @Resource
    private IdentifierGenerator identifierGenerator;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("修改IdWorker默认生成规则, {}", identifierGenerator);
        IdWorker.setIdentifierGenerator(identifierGenerator);
    }

}
