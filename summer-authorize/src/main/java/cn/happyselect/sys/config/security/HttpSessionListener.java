package cn.happyselect.sys.config.security;

import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.service.MessageBizService;
import cn.happyselect.sys.constant.DestinationConstant;
import cn.happyselect.sys.constant.MsgTypeConstant;
import cn.happyselect.sys.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.events.AbstractSessionEvent;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.stereotype.Component;

/**
 * session监听器
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-19
 */
@Component
@Slf4j
public class HttpSessionListener implements ApplicationListener<AbstractSessionEvent> {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onApplicationEvent(AbstractSessionEvent event) {
        BoundValueOperations<String, String> valueOps = stringRedisTemplate.boundValueOps(RedisKeyConstant.ONLINE_USER_COUNT);
        Long onlineUserCount = null;
        if (event instanceof SessionCreatedEvent) {
            onlineUserCount = valueOps.increment();
            log.info("SessionCreatedEvent:{}", event.getSessionId());
        } else if (event instanceof SessionDeletedEvent) {
            onlineUserCount = valueOps.decrement();
            log.info("SessionDeletedEvent:{}", event.getSessionId());
        } else if (event instanceof SessionExpiredEvent) {
            onlineUserCount = valueOps.decrement();
            log.info("SessionExpiredEvent:{}", event.getSessionId());
        }

        WsMessageDto dto = new WsMessageDto();
        dto.setContent(onlineUserCount.toString());
        dto.setMsgType(MsgTypeConstant.TEXT);
        dto.setDestination(DestinationConstant.TOPIC_ONLINE_USER_COUNT);
        MessageBizService.sendToTopic(dto);
    }

}
