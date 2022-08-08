package com.summer.generator.model;

import lombok.Data;

/**
 * API信息
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/7/31
 */
@Data
public class ApiInfo {

    /**
     * 实体信息
     */
    private EntityInfo entityInfo;

    /**
     * 类名
     */
    private String className;

    /**
     * 请求映射
     */
    private String requestMapping;
}
