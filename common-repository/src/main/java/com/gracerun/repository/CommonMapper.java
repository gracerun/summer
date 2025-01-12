package com.gracerun.repository;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.gracerun.log.annotation.Logging;
import com.gracerun.log.constant.Level;
import com.gracerun.log.serializer.ToBlankSerializer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 公共抽象Mapper接口类
 *
 * @author damoguyansi
 * @date 2022-01-01
 */
@Mapper
public interface CommonMapper<E> extends BaseMapper<E> {

    @Logging(level = Level.OFF)
    default QueryChainWrapper<E> query() {
        return ChainWrappers.queryChain(this);
    }

    @Logging(level = Level.OFF)
    default LambdaQueryChainWrapper<E> lambdaQuery() {
        return ChainWrappers.lambdaQueryChain(this);
    }

    @Logging(level = Level.OFF)
    default UpdateChainWrapper<E> update() {
        return ChainWrappers.updateChain(this);
    }

    @Logging(level = Level.OFF)
    default LambdaUpdateChainWrapper<E> lambdaUpdate() {
        return ChainWrappers.lambdaUpdateChain(this);
    }

    @Override
    @Logging
    int insert(E entity);

    @Override
    @Logging
    int deleteById(Serializable id);

    @Override
    @Logging
    int deleteById(E entity);

    @Override
    @Logging
    int deleteByMap(@Param("cm") Map<String, Object> columnMap);

    @Override
    @Logging(serializeArgsUsing = ToBlankSerializer.class)
    int delete(@Param("ew") Wrapper<E> queryWrapper);

    @Override
    @Logging
    int deleteBatchIds(@Param("coll") Collection<?> idList);

    @Override
    @Logging
    int updateById(@Param("et") E entity);

    @Override
    @Logging
    int update(@Param("et") E entity, @Param("ew") Wrapper<E> updateWrapper);

    @Override
    @Logging
    E selectById(Serializable id);

    @Override
    @Logging(serializeArgsUsing = ToBlankSerializer.class)
    default E selectOne(@Param("ew") Wrapper<E> queryWrapper) {
        List<E> ts = this.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(ts)) {
            if (ts.size() != 1) {
                throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records", new Object[0]);
            } else {
                return ts.get(0);
            }
        } else {
            return null;
        }
    }

}
