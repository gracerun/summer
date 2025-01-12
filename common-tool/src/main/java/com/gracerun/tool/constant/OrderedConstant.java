package com.gracerun.tool.constant;

import org.springframework.core.Ordered;

public interface OrderedConstant {

    /**
     * 日志处理、接口性能监控AOP,顺序最外层
     */
    int SERVICE = Ordered.HIGHEST_PRECEDENCE;

    /**
     * 当前用户信息
     */
    int USER_AWARE = SERVICE + 100;

    /**
     * 事务AOP
     */
    int TRANSACTION = Integer.MAX_VALUE;

    /**
     * 第三方接口AOP
     */
    int THIRD_SERVICE = Integer.MAX_VALUE;

    /**
     * 签名处理
     */
    int SIGN = SERVICE + 100;

    /**
     * 商户池切面
     */
    int CUSTOMER_POOL = SIGN + 100;

    /**
     * 交易路由
     */
    int CHANNEL_ROUTE = CUSTOMER_POOL + 100;

}
