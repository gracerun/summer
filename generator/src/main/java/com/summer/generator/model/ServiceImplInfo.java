package com.summer.generator.model;

import lombok.Data;

/**
 * 业务实现信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class ServiceImplInfo {

    private String className;

    private String packageStr;

    private String serviceType;

    private String voType;

    private String dtoType;

    private String entityDesc;

    private String lowerEntityName;

    private String entityName;

    private String entityPackage;

    private String daoType;

    private VoInfo voInfo;

    private DtoInfo dtoInfo;

    private ServiceInfo serviceInfo;

}
