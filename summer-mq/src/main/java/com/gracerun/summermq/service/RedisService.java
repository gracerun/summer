package com.gracerun.summermq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis服务
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Slf4j
public class RedisService {

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 加锁
     *
     * @param lockKey
     * @param seconds
     * @param supplier
     * @return
     */
    public boolean withRenderLock(String lockKey, long seconds, Supplier<Boolean> supplier) {
        String uuid = UUID.randomUUID().toString();
        boolean lockStatus = tryLock(lockKey, uuid, seconds);
        if (!lockStatus) {
            return false;
        }

        try {
            return supplier.get();
        } finally {
            releaseLock(lockKey, uuid);
        }
    }

    /**
     * redis分布式锁
     *
     * @param key     加锁键
     * @param value   加锁唯一标识,推荐使用UUID
     * @param seconds 锁过期时间,秒
     * @return
     */
    public Boolean tryLock(String key, String value, long seconds) {
        try {
            return stringRedisTemplate.execute((RedisCallback<Boolean>) (connection) ->
                    connection.set(stringRedisTemplate.getStringSerializer().serialize(key),
                            stringRedisTemplate.getStringSerializer().serialize(value),
                            Expiration.from(seconds, TimeUnit.SECONDS),
                            RedisStringCommands.SetOption.ifAbsent())
            );
        } catch (QueryTimeoutException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 与 tryLock 相对应，用作释放锁
     *
     * @param key   释放锁键
     * @param value 释放锁唯一标识,必须与获取锁的value相同
     * @return
     */
    public Boolean releaseLock(String key, String value) {
        try {
            return stringRedisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                Long result = redisConnection.eval(serializer.serialize(RELEASE_LOCK_SCRIPT),
                        ReturnType.INTEGER, 1, serializer.serialize(key), serializer.serialize(value));
                if (RELEASE_SUCCESS.equals(result)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            });
        } catch (QueryTimeoutException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

}
