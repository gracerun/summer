package com.summer.generator.model.pojo.ui;

import lombok.Data;

import java.io.Serializable;

/**
 * Ui配置
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class UiConfig implements Serializable {

    /**
     * 字段name
     */
    private String key;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 查询结果类型
     */
    private String resultType;
    /**
     * 组件
     */
    private String component;
    /**
     * 是否范围查询
     */
    private boolean rangeQuery;
    /**
     * 是否作为查询条件中
     */
    private boolean useForQuery;
    /**
     * 是否用于新增
     */
    private boolean userForAdd;
    /**
     * 是否用于修改
     */
    private boolean userForUpdate;
    /**
     * 是否用于详情
     */
    private boolean userForDetails;
    /**
     * 是否用于查询结果列表
     */
    private boolean useForResult;
    /**
     * 是否用于字典
     */
    private boolean useForDict;
    /**
     * 字典KEY
     */
    private Object dictKey;
    /**
     * 备注
     */
    private String remark;
    /**
     * 查询条件排序
     */
    private int querySort;
    /**
     * 新增排序
     */
    private int addSort;
    /**
     * 修改排序
     */
    private int updateSort;
    /**
     * 时间格式
     */
    private String formatDate;

}
