package com.summer.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.summer.base.bean.dto.QueryBaseDto;
import com.summer.base.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 基础业务处理类
 *
 * @author x09
 * @version V1.0
 * @since 2018-07-20
 **/
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> implements BaseService<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected M mapper;
    protected Class<?> entityClass = this.currentModelClass();

    /**
     * 获取泛型
     *
     * @return
     */
    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), 1);
    }

    @Override
    public T selectOne(T entity) {
        return mapper.selectOne(new QueryWrapper<>(entity));
    }

    @Override
    public List<T> selectList(T entity) {
        return mapper.selectList(new QueryWrapper<>(entity));
    }

    @Override
    public T selectById(Serializable id) {
        return mapper.selectById(id);
    }

    @Override
    public Long selectCount(QueryBaseDto queryDto) {
        return Long.valueOf(mapper.selectCount(queryDto.queryWrapper()));
    }

    @Override
    public int insert(T entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        if (entity.getLastUpdateTime() == null) {
            entity.setLastUpdateTime(new Date());
        }
        entity.setOptimistic(0);
        return mapper.insert(entity);
    }

    @Override
    public int delete(T entity) {
        return mapper.delete(new QueryWrapper<>(entity));
    }

    @Override
    public int deleteById(Serializable id) {
        return mapper.deleteById(id);
    }

    @Override
    public int updateById(T entity) {
        entity.setLastUpdateTime(new Date());
        return mapper.updateById(entity);
    }

    @Override
    public List<T> selectByQuery(QueryBaseDto queryDto) {
        return mapper.selectList(queryDto.queryWrapper());
    }

    @Override
    public List<T> selectByQuery(Wrapper<T> queryWrapper) {
        return mapper.selectList(queryWrapper);
    }

    @Override
    public IPage<T> selectPageByQuery(Page<T> page, QueryBaseDto queryDto) {
        QueryWrapper<T> queryWrapper = queryDto.queryWrapper(true);
        queryWrapper.orderByDesc("id");
        return mapper.selectPage(page, queryWrapper);
    }


    /**
     * 分页查询
     *
     * @param queryDto 条件参数封装信息
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @return 分页信息
     */
    @Override
    public IPage<T> findByPage(QueryBaseDto queryDto, Integer pageNum, Integer pageSize) {
        return selectPageByQuery(new Page<>(pageNum, pageSize), queryDto);
    }

    /**
     * 根据实体查询
     *
     * @param queryDto
     * @return
     */
    @Override
    public List<T> findBy(QueryBaseDto queryDto) {
        return selectByQuery(queryDto);
    }

    @Override
    public T findById(Serializable id) {
        return selectById(id);
    }

    @Override
    public boolean batchInsert(List<T> entityList) {
        return batchInsert(entityList, 1000);
    }

    @Override
    public boolean batchInsert(List<T> entityList, int batchSize) {
        String sqlStatement = SqlHelper.table(entityClass).getSqlStatement(SqlMethod.INSERT_ONE.getMethod());
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            entity.setOptimistic(0);
            if (entity.getCreateTime() == null) {
                entity.setCreateTime(new Date());
            }
            if (entity.getLastUpdateTime() == null) {
                entity.setLastUpdateTime(new Date());
            }
            sqlSession.insert(sqlStatement, entity);
        });
    }

    @Override
    public IPage<T> findPageByWrapper(LambdaQueryWrapper<T> queryWrapper, Integer pageNum, Integer pageSize) {
        return mapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public Long selectCount(LambdaQueryWrapper<T> queryWrapper) {
        return Long.valueOf(mapper.selectCount(queryWrapper));
    }

    /**
     * 批量执行sql
     *
     * @param list      参数集合
     * @param batchSize 批量值
     * @param consumer  消费者
     * @return
     */
    private <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Assert.isFalse(batchSize < 1, "batchSize 不能小于1");
        return executeBatch((sqlSession) -> {
            int size = list.size();
            int i = 1;

            for (Iterator item = list.iterator(); item.hasNext(); ++i) {
                E element = (E) item.next();
                consumer.accept(sqlSession, element);
                if (i % batchSize == 0 || i == size) {
                    sqlSession.flushStatements();
                }
            }
        });
    }

    /**
     * 批量执行
     *
     * @param consumer
     * @return
     */
    private boolean executeBatch(Consumer<SqlSession> consumer) {
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(entityClass);
        SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
        boolean transaction = TransactionSynchronizationManager.isSynchronizationActive();
        SqlSession sqlSession;
        if (sqlSessionHolder != null) {
            sqlSession = sqlSessionHolder.getSqlSession();
            sqlSession.commit(!transaction);
        }

        sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        try {
            consumer.accept(sqlSession);
            sqlSession.commit(!transaction);
            return true;
        } catch (Exception e) {
            sqlSession.rollback();
            log.error("{}", e);
            throw new RuntimeException("批量提交失败");
        } finally {
            sqlSession.close();
        }
    }

}

