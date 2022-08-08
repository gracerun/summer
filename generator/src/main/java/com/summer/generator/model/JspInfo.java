package com.summer.generator.model;

import lombok.Data;

/**
 * Jsp信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/4/1
 */
@Data
public class JspInfo {

    /**
     * 实体描述
     */
    private String entityDesc;

    /**
     * 类名
     */
    private String className;

    /**
     * 请求路径
     */
    private String requestMapping;

    /**
     * 实体信息
     */
    private EntityInfo entityInfo;
}
