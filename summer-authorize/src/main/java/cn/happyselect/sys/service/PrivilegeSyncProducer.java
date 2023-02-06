package cn.happyselect.sys.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 权限更新消息生产者
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-25
 */
@Component
@Slf4j
public class PrivilegeSyncProducer {

    public static final String PRIVILEGE_SYNC = "PrivilegeSync";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void sendPrivilegeChangeMessage() {
        stringRedisTemplate.convertAndSend(PRIVILEGE_SYNC, "权限数据热更新");
    }
}
