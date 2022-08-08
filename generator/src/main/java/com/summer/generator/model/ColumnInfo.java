package com.summer.generator.model;

import lombok.Data;

/**
 * 列信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class ColumnInfo {

    private String name;
    private String type;
    private String remark;
    private int len;
    private int precision;
    private String jdbcType;
    private String dict;
    private boolean nullAble;

}
