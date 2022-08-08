package com.summer.base.bean.dto;

/**
 * 比较器
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2018/9/16
 */
public enum Comparer {
    /**
     * 相等
     */
    EQU("eq"),
    /**
     * 不等于
     */
    NEQ("ne"),
    /**
     * 大于
     */
    GTR("gt"),
    /**
     * 大于等于
     */
    GEQ("ge"),
    /**
     * 小于
     */
    LSS("lt"),
    /**
     * 小于等于
     */
    LEQ("le"),
    /**
     * 模糊查询
     */
    LIKE("like"),
    /**
     * 模糊查询 - 非关键词
     */
    NOT_LIKE("notLike"),
    /**
     * 左模糊查询
     */
    LEFT_LIKE("likeLeft"),
    /**
     * 右模糊查询
     */
    RIGHT_LIKE("likeRight"),
    /**
     * 包含
     */
    IN("in"),
    /**
     * 区间
     */
    INTERVAL("between"),
    /**
     * 区间外
     */
    NOT_INTERVAL("notBetween");

    String code;

    Comparer(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
