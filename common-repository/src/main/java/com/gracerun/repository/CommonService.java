package com.gracerun.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gracerun.log.annotation.Logging;

/**
 * 公共抽象接口类
 *
 * @author damoguyansi
 * @date 2022-01-01
 */
@Logging
public interface CommonService<T> extends IService<T> {
}
