package com.summer.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 翻译标识
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/9/18
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Translation {

    /**
     * 翻译标识 - 默认开启
     *
     * @return
     */
    boolean flag() default true;
}
