package cn.happyselect.sys.constant;

/**
 * 消息状态
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-09-22
 */
public interface MessageStatusConstant {

    /**
     * 消息初始化,等待发送
     */
    String WAIT_SEND = "WAIT_SEND";

    /**
     * 发送消息成功
     */
    String SUCCESS = "SUCCESS";

    /**
     * 发送消息失败
     */
    String FAIL = "FAIL";

}
