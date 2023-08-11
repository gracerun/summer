package com.gracerun.idempotent.repository;


import com.gracerun.idempotent.pojo.IdempotentEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务实体持久化
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/15
 */
public interface IdempotentRepository {

    /**
     * 查询数据是否已存在
     */
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    boolean exists(IdempotentEntity entity);

    /**
     * 保存数据
     */
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    boolean create(IdempotentEntity entity);

}
