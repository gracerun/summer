package com.summer.generator.model;

import lombok.Data;

/**
 * MapperXml信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class MapperXmlInfo {

    private String nameSpace;

    private String className;

    private ServiceInfo serviceInfo;

    private EntityInfo entityInfo;

    private VoInfo voInfo;

}
