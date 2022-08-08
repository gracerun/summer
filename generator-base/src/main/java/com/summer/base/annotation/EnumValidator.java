package com.summer.base.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举验证
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/8/6
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValidatorHandler.class})
public @interface EnumValidator {

    /**
     * 提示信息
     */
    String message() default "枚举值错误";

    /**
     * 目标枚举类
     */
    Class<?>[] target();

    /**
     * 分组验证 - 暂未实现
     */
    Class<?>[] groups() default {};

    /**
     * 载荷
     */
    Class<? extends Payload>[] payload() default {};
}
