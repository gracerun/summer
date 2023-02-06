package cn.happyselect.sys.constant;

/**
 * redis广播消息主题
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-21
 */
public interface RedisChannelTopicConstant {

    /**
     * websocket主题
     */
    String WEBSOCKET_TOPIC = RedisKeyConstant.NAMESPACE + "websocketTopic";

}
