package cn.happyselect.sys.config.redis;

import cn.happyselect.sys.constant.RedisKeyConstant;
import com.gracerun.log.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * webSocketsession服务
 */
@Component
@Slf4j
public class RedisIndexedWebSocketSessionRepository {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void saveWsSession(String httpSessionId, String wsSessionId) {
        if (IpUtil.getIp() == null) {
            log.warn("IpUtil.getIp() is null!");
            return;
        }
        log.info("saveWsSession httpSessionId={},wsSessionId={},localIp={}", httpSessionId, wsSessionId, IpUtil.getIp());
        BoundHashOperations<String, Object, Object> hash = stringRedisTemplate.boundHashOps(RedisKeyConstant.HTTP_SESSION_ID_WS_IP_MAP + httpSessionId);
        hash.put(wsSessionId, IpUtil.getIp());
        hash.expire(1 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public void deleteByHttpSessionId(String httpSessionId) {
        try {
            log.info("deleteByHttpSessionId httpSessionId={}", httpSessionId);
            stringRedisTemplate.delete(RedisKeyConstant.HTTP_SESSION_ID_WS_IP_MAP + httpSessionId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void deleteByWsSessionId(String httpSessionId, String wsSessionId) {
        log.info("deleteByWsSessionId httpSessionId={},wsSessionId={}", httpSessionId, wsSessionId);
        Set<Object> keys = stringRedisTemplate.boundHashOps(RedisKeyConstant.HTTP_SESSION_ID_WS_IP_MAP + httpSessionId).keys();
        if (!CollectionUtils.isEmpty(keys)) {
            keys.remove(wsSessionId);
            if (keys.isEmpty()) {
                stringRedisTemplate.delete(RedisKeyConstant.HTTP_SESSION_ID_WS_IP_MAP + httpSessionId);
            } else {
                stringRedisTemplate.boundHashOps(RedisKeyConstant.HTTP_SESSION_ID_WS_IP_MAP + httpSessionId).delete(wsSessionId);
            }
        }
    }

}
