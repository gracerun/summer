package cn.happyselect.sys.constant;

/**
 * 消息目的地
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-21
 */
public interface DestinationConstant {

    /**
     * 用户目的地前缀
     */
    String USER_PREFIX = "/user";

    /**
     * 广播前缀
     */
    String TOPIC_PREFIX = "/topic";

    /**
     * 一对一消息前缀
     */
    String QUEUE_PREFIX = "/queue";

    /**
     * 一对一消息发送地址
     */
    String QUEUE_MESSAGE = QUEUE_PREFIX + "/message";

    /**
     * 在线用户发送地址
     */
    String TOPIC_ONLINE_USER_COUNT = TOPIC_PREFIX + "/onlineUserCount";

}
