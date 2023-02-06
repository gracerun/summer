package cn.happyselect.sys.config.websocket;

import cn.happyselect.sys.config.redis.RedisIndexedWebSocketSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.session.web.socket.handler.WebSocketRegistryListener;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket session监听器
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-19
 * @see WebSocketRegistryListener
 */
@Component
@Slf4j
public final class WebSocketSessionRegistryListener implements ApplicationListener<ApplicationEvent> {

    static final CloseStatus SESSION_EXPIRED_STATUS = new CloseStatus(CloseStatus.POLICY_VIOLATION.getCode(),
            "This connection was established under an authenticated HTTP Session that has expired");

    private final ConcurrentHashMap<String, Map<String, WebSocketSession>> httpSessionIdToWsSessions = new ConcurrentHashMap<>();

    @Autowired
    private RedisIndexedWebSocketSessionRepository redisIndexedWebSocketSessionRepository;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionDestroyedEvent) {
            SessionDestroyedEvent e = (SessionDestroyedEvent) event;
            closeWsSessions(e.getSessionId());
            redisIndexedWebSocketSessionRepository.deleteByHttpSessionId(e.getSessionId());
        } else if (event instanceof SessionConnectEvent) {
            SessionConnectEvent e = (SessionConnectEvent) event;
            afterConnectionEstablished(e.getWebSocketSession());
        } else if (event instanceof SessionDisconnectEvent) {
            SessionDisconnectEvent e = (SessionDisconnectEvent) event;
            Map<String, Object> sessionAttributes = SimpMessageHeaderAccessor
                    .getSessionAttributes(e.getMessage().getHeaders());
            String httpSessionId = (sessionAttributes != null)
                    ? SessionRepositoryMessageInterceptor.getSessionId(sessionAttributes) : null;
            afterConnectionClosed(httpSessionId, e.getSessionId());
            redisIndexedWebSocketSessionRepository.deleteByWsSessionId(httpSessionId, e.getSessionId());
        }
    }

    private void afterConnectionEstablished(WebSocketSession wsSession) {
        Principal principal = wsSession.getPrincipal();
        if (principal == null) {
            return;
        }

        String httpSessionId = getHttpSessionId(wsSession);
        registerWsSession(httpSessionId, wsSession);
        redisIndexedWebSocketSessionRepository.saveWsSession(httpSessionId, wsSession.getId());
    }

    private String getHttpSessionId(WebSocketSession wsSession) {
        Map<String, Object> attributes = wsSession.getAttributes();
        return SessionRepositoryMessageInterceptor.getSessionId(attributes);
    }

    private void afterConnectionClosed(String httpSessionId, String wsSessionId) {
        if (httpSessionId == null) {
            return;
        }

        Map<String, WebSocketSession> sessions = this.httpSessionIdToWsSessions.get(httpSessionId);
        if (sessions != null) {
            boolean result = sessions.remove(wsSessionId) != null;
            if (log.isDebugEnabled()) {
                log.debug("Removal of " + wsSessionId + " was " + result);
            }
            if (sessions.isEmpty()) {
                this.httpSessionIdToWsSessions.remove(httpSessionId);
                if (log.isDebugEnabled()) {
                    log.debug("Removed the corresponding HTTP Session for " + wsSessionId
                            + " since it contained no WebSocket mappings");
                }
            }
        }
    }

    private void registerWsSession(String httpSessionId, WebSocketSession wsSession) {
        Map<String, WebSocketSession> sessions = this.httpSessionIdToWsSessions.get(httpSessionId);
        if (sessions == null) {
            sessions = new ConcurrentHashMap<>();
            this.httpSessionIdToWsSessions.putIfAbsent(httpSessionId, sessions);
            sessions = this.httpSessionIdToWsSessions.get(httpSessionId);
        }
        sessions.put(wsSession.getId(), wsSession);
    }

    private void closeWsSessions(String httpSessionId) {
        Map<String, WebSocketSession> sessionsToClose = this.httpSessionIdToWsSessions.remove(httpSessionId);
        if (sessionsToClose == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Closing WebSocket connections associated to expired HTTP Session " + httpSessionId);
        }
        for (WebSocketSession toClose : sessionsToClose.values()) {
            try {
                toClose.close(SESSION_EXPIRED_STATUS);
            } catch (IOException ex) {
                log.debug("Failed to close WebSocketSession (this is nothing to worry about but for debugging only)",
                        ex);
            }
        }
    }

}
