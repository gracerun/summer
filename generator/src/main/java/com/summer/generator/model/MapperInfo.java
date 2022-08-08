package com.summer.generator.model;

import lombok.Data;

/**
 * Mapper信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class MapperInfo {

    /**
     * XXXMapper.xml
     */
    private String fileName;

    private String namespace;

    private DaoInfo daoInfo;

    private EntityInfo entityInfo;


}
