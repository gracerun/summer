package com.summer.generator.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 实体信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class EntityInfo {

    /**
     * 实体名
     */
    private String entityName;

    /**
     * 实体描述
     */
    private String entityDesc;

    /**
     * 实体所在包路径
     */
    private String entityPackage;

    /**
     * 实体类名
     */
    private String className;

    /**
     * 包路径 + 类名
     */
    private String packageClassName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 需要导入的包
     */
    private Set<String> imports = new HashSet<>();

    /**
     * 属性名以及对应的类型
     */
    private Map<String, String> propTypes;

    /**
     * 属性名以及注释的对应
     */
    private Map<String, String> propRemarks;

    /**
     * 属性名和jdbc类型的映射
     */
    private Map<String, String> propJdbcTypes;

    /**
     * 属性名和字段名的映射
     */
    private Map<String, String> propNameColumnNames;
    /**
     * 属性名和字段长度的映射
     */
    private Map<String, Integer> propLength;
    /**
     * 属性名和字段精度的映射
     */
    private Map<String, Integer> precisions;
    /**
     * 属性名和字典的映射
     */
    private Map<String, String> dicts;
    /**
     * 属性名和是否为null
     */
    private Map<String, Boolean> nullAbles;

}
