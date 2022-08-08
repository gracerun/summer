package com.summer.base.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summer.base.bean.dto.QueryBaseDto;
import com.summer.base.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 基础业务处理接口
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2018/7/31
 */
public interface BaseService<T extends BaseEntity> {

    /**
     * 根据实体-查询单个结果
     *
     * @param entity
     * @return
     */
    T selectOne(T entity);

    /**
     * 根据实体-查询多个结果
     *
     * @param entity
     * @return
     */
    List<T> selectList(T entity);

    /**
     * 根据ID-查询
     *
     * @param id
     * @return
     */
    T selectById(Serializable id);

    /**
     * 根据实体-统计查询
     *
     * @param queryDto
     * @return
     */
    Long selectCount(QueryBaseDto queryDto);

    /**
     * 实体-新增
     *
     * @param entity
     */
    int insert(T entity);

    /**
     * 根据实体-删除
     *
     * @param entity
     */
    int delete(T entity);

    /**
     * 根据ID-删除
     *
     * @param id
     */
    int deleteById(Serializable id);

    /**
     * 根据ID-更新
     *
     * @param entity
     */
    int updateById(T entity);

    /**
     * 根据实体查询-查询
     *
     * @param queryDto
     * @return
     */
    List<T> selectByQuery(QueryBaseDto queryDto);

    /**
     * 根据装饰器查询-查询
     *
     * @param queryWrapper
     * @return
     */
    List<T> selectByQuery(Wrapper<T> queryWrapper);

    /**
     * 根据Page、实体查询-查询分页
     *
     * @param page
     * @param queryDto
     * @return
     */
    IPage<T> selectPageByQuery(Page<T> page, QueryBaseDto queryDto);


    /**
     * 分页查询
     *
     * @param queryDto
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<T> findByPage(QueryBaseDto queryDto, Integer pageNum, Integer pageSize);

    /**
     * 查询列表
     *
     * @param queryDto
     * @return
     */
    List<T> findBy(QueryBaseDto queryDto);


    /**
     * 根据ID-查询
     *
     * @param id
     * @return
     */
    T findById(Serializable id);

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    boolean batchInsert(List<T> entityList, int batchSize);

    /**
     * 批量插入 - 默认1000条一批
     *
     * @param entityList
     * @return
     */
    boolean batchInsert(List<T> entityList);

    /**
     * 分页条件查询
     *
     * @param queryWrapper
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<T> findPageByWrapper(LambdaQueryWrapper<T> queryWrapper, Integer pageNum, Integer pageSize);

    /**
     * 根据条件-统计查询
     *
     * @param queryWrapper
     * @return
     */
    Long selectCount(LambdaQueryWrapper<T> queryWrapper);
}
