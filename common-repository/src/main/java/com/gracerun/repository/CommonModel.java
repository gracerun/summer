package com.gracerun.repository;

import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 抽象实体类：无公共字段
 *
 * @author damoguyansi
 * @date 2022-01-01
 **/
public abstract class CommonModel<T extends Model<?>> extends Model<T> {

}
