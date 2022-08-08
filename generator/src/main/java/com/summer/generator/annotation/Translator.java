package com.summer.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 翻译器
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/8/27
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Translator {

    /**
     * 翻译类型
     */
    TranslatorType type() default TranslatorType.DICTIONARY;

    /**
     * 目标字典类型名
     */
    String targetName() default "";

    /**
     * 敏感数据 - 开始位置
     * 0 - n
     */
    int begin() default 4;

    /**
     * 敏感数据 - 结束位置(默认 len - 4)
     * 值为负数 则取长度+该值
     * 值为正数 则取该值的索引
     */
    int end() default -4;

    /**
     * 敏感数据符号
     */
    String symbol() default "*";

    /**
     * 敏感数据长度
     */
    int len() default 8;

    /**
     * 分组 - 暂未实现
     */
    Class<?>[] groups() default {};

    /**
     * 业务类型 属性名称 - 业务名称时必填，否则不处理
     */
    String businessType() default "";
}
