package com.summer.generator.model;

import lombok.Data;

/**
 * 控制器信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class ControllerInfo {

    private String packageStr;
    private VoInfo voInfo;
    private DtoInfo dtoInfo;
    private String className;
    private ServiceInfo serviceInfo;
    private String entityName;
    private String requestMapping;
    private String entityPackage;
    private String entityDesc;

}
