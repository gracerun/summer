package com.gracerun.message.service;


import com.gracerun.idempotent.pojo.IdempotentEntity;
import com.gracerun.idempotent.repository.IdempotentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 幂等校验
 *
 * @author Tom
 * @date 2023-06-15
 **/
@Service
@Slf4j
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class IdempotentDataBizService implements IdempotentRepository {

    @Override
    public boolean exists(IdempotentEntity entity) {
        log.warn("This is mock");
        return false;
    }

    @Override
    public boolean create(IdempotentEntity entity) {
        log.warn("This is mock");
        return true;
    }
}
