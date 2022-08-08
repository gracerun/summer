package com.summer.generator.model;

import lombok.Data;

import java.util.List;

/**
 * 表信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class TableInfo {

    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 备注
     */
    private String remark;
    /**
     * 列信息
     */
    private List<ColumnInfo> columnList;

}
