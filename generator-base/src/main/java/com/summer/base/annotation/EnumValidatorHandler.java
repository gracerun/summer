package com.summer.base.annotation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 枚举验证处理
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/8/6
 */
@Slf4j
public class EnumValidatorHandler implements ConstraintValidator<EnumValidator, String> {

    /**
     * 目标类集合
     */
    protected Class<?>[] classes;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        // 如果目标枚举类 不存在，则不校验
        if (classes.length == 0) {
            return true;
        }

        // 如果值为null 则不校验
        if (value == null) {
            return true;
        }

        AtomicBoolean flag = new AtomicBoolean(false);
        Arrays.asList(classes).forEach(clazz -> {
            if (clazz.isEnum()) {
                Object[] objects = clazz.getEnumConstants();
                Method method;
                try {
                    method = clazz.getMethod("name");
                } catch (NoSuchMethodException e) {
                    log.error("获取枚举:[{}], 方法名 异常 : {}", clazz.getName(), e);
                    return;
                }

                Arrays.asList(objects).forEach(obj -> {
                    String code;
                    try {
                        code = (String) method.invoke(obj, null);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("获取枚举:[{}], 属性名 异常 : {}", clazz.getName(), e);
                        return;
                    }
                    if (Objects.equals(code, value)) {
                        flag.set(true);
                        return;
                    }
                });

                if (flag.get()) {
                    return;
                }
            }
        });

        return flag.get();
    }

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        classes = constraintAnnotation.target();
    }
}
