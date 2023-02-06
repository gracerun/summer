package cn.happyselect.sys.bean;

import cn.happyselect.base.enums.ClientType;

/**
 * 短信dto接口
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-20
 */
public interface ShortMessageDtoInterface {

    /**
     * 手机号
     */
    String getPhone();

    /**
     * 短信类型
     */
    String getMessageType();

    /**
     * 客户端类型
     * @see ClientType
     */
    String getClientType();



}
