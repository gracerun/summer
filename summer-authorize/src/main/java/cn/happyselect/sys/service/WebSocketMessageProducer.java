package cn.happyselect.sys.service;

import cn.happyselect.sys.bean.dto.MessageListDto;
import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.constant.RedisChannelTopicConstant;
import cn.happyselect.sys.constant.RedisKeyConstant;
import com.alibaba.fastjson.JSON;
import com.gracerun.log.core.TraceRunnableWrapper;
import com.gracerun.summermq.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息生产者
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-13
 */
@Component
@ConditionalOnBean(annotation = {EnableWebSocketMessageBroker.class})
@Slf4j
public class WebSocketMessageProducer<S extends Session> {



    private final ThreadPoolExecutor producerThread = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FindByIndexNameSessionRepository<S> sessionRepository;

    public void push(WsMessageDto msg) {
        try {
            if (Objects.nonNull(msg)) {
                producerThread.execute(new TraceRunnableWrapper(new Producer(msg)));
            }
        } catch (Exception e) {
            log.error("push fail");
            log.error(e.getMessage(), e);
        }
    }

    public void pushAll(MessageListDto data) {
        try {
            if (!CollectionUtils.isEmpty(data.getMsgList())) {
                producerThread.execute(new TraceRunnableWrapper(new Producer(data.getMsgList())));
            }
        } catch (Exception e) {
            log.error("pushAll fail");
            log.error(e.getMessage(), e);
        }
    }

    class Producer implements Runnable {

        private List<WsMessageDto> msgList;

        private WsMessageDto msg;

        public Producer(List<WsMessageDto> msgList) {
            this.msgList = msgList;
        }

        public Producer(WsMessageDto msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                if (Objects.nonNull(msg)) {
                    push(msg);
                } else if (!CollectionUtils.isEmpty(msgList)) {
                    for (WsMessageDto msg : msgList) {
                        push(msg);
                    }
                }
            } catch (Exception e) {
                log.error("Producer error");
                log.error(e.getMessage(), e);
            }
        }

        private void push(WsMessageDto msg) {
            if (StringUtils.hasText(msg.getToWsSessionId())) {
                Map<Object, Object> wsSessionIdMap = findWsSessionServerIPByHttpSessionId(msg.getToHttpSessionId());
                if (!CollectionUtils.isEmpty(wsSessionIdMap)) {
                    wsSessionIdMap.forEach((wsSessionId, ip) -> {
                        if (msg.getToWsSessionId().equals(wsSessionId)) {
                            log.info("leftPush {} 1 psc", ip);
                            msg.setToHttpSessionId(null);
                            stringRedisTemplate.opsForList().leftPush(RedisKeyConstant.MSG_QUEUE + ip, JSON.toJSONString(msg));
                        }
                    });
                }
            } else if (StringUtils.hasText(msg.getToHttpSessionId())) {
                Map<Object, Object> wsSessionIdMap = findWsSessionServerIPByHttpSessionId(msg.getToHttpSessionId());
                if (!CollectionUtils.isEmpty(wsSessionIdMap)) {
                    wsSessionIdMap.forEach((wsSessionId, ip) -> {
                        log.info("leftPush {} 1 psc", ip);
                        msg.setToWsSessionId((String) wsSessionId);
                        msg.setToHttpSessionId(null);
                        stringRedisTemplate.opsForList().leftPush(RedisKeyConstant.MSG_QUEUE + ip, JSON.toJSONString(msg));
                    });
                }
            } else if (StringUtils.hasText(msg.getToUserId())) {
                Set<String> wsSessionServerIP = findWsSessionServerIPByUserId(msg.getToUserId());
                if (!CollectionUtils.isEmpty(wsSessionServerIP)) {
                    log.info("leftPush {} 1 psc", wsSessionServerIP);
                    wsSessionServerIP.forEach(ip -> stringRedisTemplate.opsForList().leftPush(RedisKeyConstant.MSG_QUEUE + ip, JSON.toJSONString(msg)));
                }
            } else {
                //发送广播消息
                stringRedisTemplate.convertAndSend(RedisChannelTopicConstant.WEBSOCKET_TOPIC, JSON.toJSONString(msg));
            }
        }

        private Set<String> findWsSessionServerIPByUserId(String userId) {
            Map<String, S> principalName = sessionRepository.findByPrincipalName(userId);
            if (CollectionUtils.isEmpty(principalName)) {
                log.info("findWsSessionServerIPByUserId userId={}, wsSessionServerIP is null", userId);
                return null;
            }
            Set<String> wsSessionServerIP = new HashSet<>();
            principalName.forEach((sessionId, session) -> {
                List<Object> values = stringRedisTemplate.boundHashOps(RedisKeyConstant.HTTP_SESSION_ID_WS_IP_MAP + sessionId).values();
                if (!CollectionUtils.isEmpty(values)) {
                    values.forEach(v -> wsSessionServerIP.add((String) v));
                }
            });
            log.info("findWsSessionServerIPByUserId userId={}, wsSessionServerIP={}", userId, wsSessionServerIP);
            return wsSessionServerIP;
        }

        private Map<Object, Object> findWsSessionServerIPByHttpSessionId(String httpSessionId) {
            Map<Object, Object> wsSessionIdAndIp = stringRedisTemplate.boundHashOps(RedisKeyConstant.HTTP_SESSION_ID_WS_IP_MAP + httpSessionId).entries();
            log.info("findWsSessionServerIPByHttpSessionId httpSessionId={}, wsSessionServerIP={}", httpSessionId, wsSessionIdAndIp);
            return wsSessionIdAndIp;
        }

    }

}


