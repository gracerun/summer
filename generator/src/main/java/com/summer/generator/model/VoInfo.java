package com.summer.generator.model;

import lombok.Data;

/**
 * Vo信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class VoInfo {

    /**
     * 包路径
     */
    private String packageStr;

    /**
     * 类名
     */
    private String className;

    /**
     * 实体信息
     */
    private EntityInfo entityInfo;


}
