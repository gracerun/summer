package com.summer.generator.model;

import lombok.Data;

/**
 * Dao信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class DaoInfo {

    private String packageStr;

    private String className;

    private EntityInfo entityInfo;

    private VoInfo voInfo;


}
