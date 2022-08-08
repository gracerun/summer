package com.summer.generator.annotation;

/**
 * 翻译接口
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/8/27
 */
public interface Translate {

    /**
     * 翻译
     *
     * @param value      待翻译值
     * @param translator 翻译器注解
     * @return 翻译后的值
     */
    String translate(String value, Translator translator);
}
