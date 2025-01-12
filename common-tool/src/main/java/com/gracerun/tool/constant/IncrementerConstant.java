package com.gracerun.tool.constant;

/**
 * 自增序列常量
 *
 * @author ce.sun
 */
public interface IncrementerConstant {

    /**
     * 数据中心前缀
     */
    String DATA_CENTER = "0";

    /**
     * 合伙人前缀
     */
    String AGENT_PRE = "10" + DATA_CENTER;

    /**
     * 商户前缀
     */
    String CUSTOMER_PRE = "20" + DATA_CENTER;

    /**
     * 二维码前缀
     */
    String QRCODE_PRE = "30" + DATA_CENTER;

    /**
     * 二维码批次编号前缀
     */
    String BATCH_NO_PRE = "40" + DATA_CENTER;

    /**
     * 二维码申请编号前缀
     */
    String RECIEPT_NO_PRE = "50" + DATA_CENTER;

    /**
     * 开票记录编号前缀
     */
    String BILL_RECORD_NO_PRE = "51" + DATA_CENTER;

    /**
     * 二维码迁移记录编号前缀
     */
    String MIGRATE_RECORD_NO_PRE = "52" + DATA_CENTER;


    /**
     * 产品编号前缀
     */
    String PRODUCT_NO_PRE = "60" + DATA_CENTER;

    /**
     * 商户商品本地编码
     */
    String GOODS_NO_PRE = "70" + DATA_CENTER;

    /**
     * 商户变更单前缀
     */
    String CHANGE_BILL_PRE = "80" + DATA_CENTER;

    /**
     * 代付订单前缀
     */
    String PROXY_ORDER_PRE = "82" + DATA_CENTER;

    /**
     * 广告前缀
     */
    String ADVERT_NO_PRE = "90" + DATA_CENTER;

    /**
     * 广告序列名称
     */
    String SEQ_ADVERT_NO = "SEQ_ADVERT_NO";

    /**
     * 二维码序列名称
     */
    String SEQ_QRCODE = "SEQ_QRCODE";

    /**
     * 开票记录序列名称
     */
    String SEQ_BILL_RECORD_NO = "SEQ_BILL_RECORD_NO";

    /**
     * 合伙人序列名称
     */
    String SEQ_AGENT_NO = "SEQ_AGENT_NO";

    /**
     * 商户序列名称
     */
    String SEQ_CUSTOMER_NO = "SEQ_CUSTOMER_NO";

    /**
     * 批次序列名称
     */
    String SEQ_BATCH_NO = "SEQ_BATCH_NO";

    /**
     * 二维码迁移记录序列名称
     */
    String SEQ_RECIEPT_NO = "SEQ_RECIEPT_NO";

    /**
     * 采购单序列名称
     */
    String SEQ_MIGRATE_NO = "SEQ_MIGRATE_NO";

    /**
     * 产品序列名称
     */
    String SEQ_PRODUCT_NO = "SEQ_PRODUCT_NO";

    /**
     * 商户商品序列名称
     */
    String SEQ_GOODS_NO = "SEQ_GOODS_NO";

    /**
     * 商户变更单
     */
    String SEQ_CHANGE_BILL_NO = "SEQ_CHANGE_BILL_NO";

    /**
     * 商户代付账号
     */
    String SEQ_PROXY_ACCOUNT_NO = "SEQ_PROXY_ACCOUNT_NO";

    /**
     * 商户代付订单号
     */
    String SEQ_PROXY_ORDER_NO = "SEQ_PROXY_ORDER_NO";

    /**
     * 快捷流水号
     */
    String SEQ_QUICK_FLOW_NO = "SEQ_QUICK_FLOW_NO";

}
