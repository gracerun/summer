package cn.happyselect.base.constants;

/**
 * Copyright (C), 2010-2020, xxx payment. Co., Ltd.
 * <p>
 * 状态常量表
 *
 * @author lowrie
 * @version 1.0.0
 * @date 2020/7/29
 */
public interface StatusConstant {


    /**
     * 初始状态
     */
    String INIT = "INIT";

    /**
     * 可用状态
     */
    String TRUE = "TRUE";

    /**
     * 不可用状态
     */
    String FALSE = "FALSE";

    /**
     * 成功状态
     */
    String SUCCESS = "SUCCESS";

    /**
     * 部分成功
     */
    String PARTIAL = "PARTIAL";

    /**
     * 处理中
     */
    String PROCESSING = "PROCESSING";

    /**
     * 失败状态
     */
    String FAIL = "FAIL";

    /**
     * 逻辑删除状态
     */
    String DELETE = "DELETE";


    /**
     * 待审核
     */
    String WAIT_AUDIT = "WAIT_AUDIT";

    /**
     * 待签约
     */
    String WAIT_SIGN= "WAIT_SIGN";

    /**
     * 审核拒绝
     */
    String REJECT = "REJECT";


    /**
     * 设备绑定状态
     */
    String BINDED = "BINDED";

    /**
     * 冻结状态
     */
    String FREEZE = "FREEZE";

    /**
     * 未绑定 - MI推广码
     */
    String UN_BIND = "UN_BIND";

}
