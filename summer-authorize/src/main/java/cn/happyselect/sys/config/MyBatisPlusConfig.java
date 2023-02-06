package cn.happyselect.sys.config;

import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C), 2010-2020, xxx payment. Co., Ltd.
 * <p>
 * mybatis-plus 支持乐观锁
 *
 * @author lowrie
 * @version 1.0.0
 * @date 2020/8/11
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }
}
