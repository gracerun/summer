package com.summer.base.annotation;

/**
 * 数据范围
 * @author adc
 * @date 6/13/21
 * @version 1.0.0
 */
public enum Scope {

    /**
     * 只包含子集,不包含自己
     */
    ONLY_CHILD,
    /**
     * 包含自己与所有的子集
     */
    ALL,
    /**
     * 只包含自己
     */
    ONLY_SELF

}
