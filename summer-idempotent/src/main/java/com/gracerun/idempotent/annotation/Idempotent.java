package com.gracerun.idempotent.annotation;

import com.gracerun.idempotent.handler.DefaultThrowException;
import com.gracerun.idempotent.handler.RejectedExecutionHandler;

import java.lang.annotation.*;

/**
 * 幂等校验
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Idempotent {

    /**
     * Spring Expression Language (SpEL) expression for computing the key dynamically.
     * 业务编号
     */
    String primaryNo();

    /**
     * Spring Expression Language (SpEL) expression for computing the key dynamically.
     * 业务类型
     */
    String primaryType();

    /**
     * 异常处理
     *
     * @return
     */
    Class<? extends RejectedExecutionHandler> handler() default DefaultThrowException.class;

}
