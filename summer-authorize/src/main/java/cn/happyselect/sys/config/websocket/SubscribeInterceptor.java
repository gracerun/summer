package cn.happyselect.sys.config.websocket;

import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.service.MessageBizService;
import cn.happyselect.sys.constant.DestinationConstant;
import cn.happyselect.sys.constant.MsgTypeConstant;
import cn.happyselect.sys.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;

import java.util.Map;

/**
 * 订阅消息拦截器
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-21
 */
@Slf4j
public class SubscribeInterceptor implements ChannelInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        MessageHeaders headers = message.getHeaders();
        SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType(headers);
        if (SimpMessageType.SUBSCRIBE.equals(messageType)) {
            log.info("{}", message);

            String destination = SimpMessageHeaderAccessor.getDestination(headers);
            String wsSessionId = SimpMessageHeaderAccessor.getSessionId(headers);
            if (DestinationConstant.TOPIC_ONLINE_USER_COUNT.equals(destination)) {
                Map<String, Object> sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(headers);
                String httpSessionId = (sessionAttributes != null)
                        ? SessionRepositoryMessageInterceptor.getSessionId(sessionAttributes) : null;

                WsMessageDto dto = new WsMessageDto();
                dto.setToHttpSessionId(httpSessionId);
                dto.setToWsSessionId(wsSessionId);
                dto.setMsgType(MsgTypeConstant.TEXT);
                dto.setContent(stringRedisTemplate.boundValueOps(RedisKeyConstant.ONLINE_USER_COUNT).get());
                dto.setDestination(DestinationConstant.TOPIC_ONLINE_USER_COUNT);
                MessageBizService.sendToTopic(dto);
            }
        }
    }
}
