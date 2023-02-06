package cn.happyselect.sys.config.websocket;

import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.constant.DestinationConstant;
import cn.happyselect.sys.constant.RedisKeyConstant;
import com.alibaba.fastjson.JSON;
import com.gracerun.log.core.TraceRunnableWrapper;
import com.gracerun.log.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息消费者
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-13
 */
@Component
@ConditionalOnBean(annotation = {EnableWebSocketMessageBroker.class})
@Slf4j
public class WebSocketMessageConsumer implements ApplicationRunner, MessageListener {

    private final ThreadPoolExecutor consumerThread = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    private final ThreadPoolExecutor pushThread = new ThreadPoolExecutor(1, 8, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        consumerThread.execute(new TraceRunnableWrapper(new Consumer()));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String s = new String(message.getBody());
        if (StringUtils.hasText(s)) {
            WsMessageDto dto = JSON.parseObject(s, WsMessageDto.class);
            pushThread.execute(new TraceRunnableWrapper(new ExecutorThread(dto)));
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            log.info("Consumer execute begin");
            while (true) {
                try {
                    //检查线程池队列是否空闲
                    int size = pushThread.getQueue().size();
                    if (size > 100) {
                        log.warn("Thread too busy, sleep 100ms");
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            log.error(e.getMessage(), e);
                            break;
                        }
                    }

                    String s = stringRedisTemplate.opsForList().rightPop(RedisKeyConstant.MSG_QUEUE + IpUtil.getIp(), 2, TimeUnit.SECONDS);
                    if (StringUtils.hasText(s)) {
                        WsMessageDto message = JSON.parseObject(s, WsMessageDto.class);
                        pushThread.execute(new TraceRunnableWrapper(new ExecutorThread(message)));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("Consumer execute end");
        }
    }

    class ExecutorThread implements Runnable {

        private WsMessageDto wsMessageDto;

        public ExecutorThread(WsMessageDto message) {
            this.wsMessageDto = message;
        }

        @Override
        public void run() {
            try {
                String destination = wsMessageDto.getDestination();
                if (destination.startsWith(DestinationConstant.QUEUE_PREFIX)) {
                    if (StringUtils.hasText(wsMessageDto.getToWsSessionId())) {
                        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
                        headerAccessor.setSessionId(wsMessageDto.getToWsSessionId());
                        headerAccessor.setLeaveMutable(true);
                        MessageHeaders messageHeaders = headerAccessor.getMessageHeaders();
                        messagingTemplate.convertAndSendToUser(wsMessageDto.getToWsSessionId(), destination, wsMessageDto, messageHeaders);
                    } else {
                        messagingTemplate.convertAndSendToUser(wsMessageDto.getToUserId(), destination, wsMessageDto);
                    }
                } else if (destination.startsWith(DestinationConstant.TOPIC_PREFIX)) {
                    messagingTemplate.convertAndSend(destination, wsMessageDto);
                } else {
                    log.warn("Not support destination:{}", destination);
                }
            } catch (MessagingException e) {
                log.error("消息发送失败:{}", e.getFailedMessage());
                log.error(e.getMessage(), e);
            }
        }
    }
}
