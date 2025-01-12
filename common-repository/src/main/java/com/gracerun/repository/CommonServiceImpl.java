package com.gracerun.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 公共抽象service实现类
 *
 * @author damoguyansi
 * @date 2022-01-01
 */
public abstract class CommonServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements CommonService<T> {
}
