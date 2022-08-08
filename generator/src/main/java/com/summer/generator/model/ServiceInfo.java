package com.summer.generator.model;

import lombok.Data;

/**
 * 业务信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class ServiceInfo {

    private String packageStr;

    private String getCommandType;

    private String listCommandType;

    private String batchCommandType;

    private String commandType;

    private String queryCommandType;

    private String voType;

    private String entityDesc;

    private String className;

    private String entityName;

    private String voClassName;

    private String dtoClassName;

    private String entityPackage;
    //private CommandInfo commandInfo;

    private VoInfo voInfo;

}
