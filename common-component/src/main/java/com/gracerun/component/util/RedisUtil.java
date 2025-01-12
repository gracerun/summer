package com.gracerun.component.util;

import com.gracerun.tool.bean.ResponseBean;
import com.gracerun.tool.constant.ResponseCode;
import com.gracerun.tool.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 说明:
 * redis工具类
 *
 * @author sck
 * @create 2018-11-02 上午11:28
 */
@Slf4j
public class RedisUtil {

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private static StringRedisTemplate template;

    @Resource
    public void setTemplate(StringRedisTemplate template) {
        RedisUtil.template = template;
    }

    public static long WEEK = 3600 * 24 * 7;
    public static long DAY = 3600 * 24;
    public static long HOUR = 3600;
    public static long MINUTE = 60;

    public static Long increment(String key) {
        try {
            return template.boundValueOps(key).increment();
        } catch (Exception e) {
            log.error("redis increment error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis increment error", e);
        }
    }

    public static Long decrement(String key) {
        try {
            return template.boundValueOps(key).decrement();
        } catch (Exception e) {
            log.error("redis decrement error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis decrement error", e);
        }
    }

    public static Set<String> keys(String pattern) {
        try {
            return template.keys(pattern);
        } catch (Exception e) {
            log.error("redis keys error, pattern: {}", pattern, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis keys error", e);
        }
    }

    public static String get(String key) {
        try {
            return template.boundValueOps(key).get();
        } catch (Exception e) {
            log.error("redis get error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis get error", e);
        }
    }

    /**
     * 从redis获取指定值
     *
     * @param key
     * @param defaultValue 当取值为空或异常时返回默认值
     * @return
     */
    public static String get(String key, String defaultValue) {
        try {
            String value = RedisUtil.get(key);
            if (!StringUtils.hasText(value)) {
                return defaultValue;
            }
            return value;
        } catch (Exception e) {
            log.error("redis get error, key: {}", key, e);
            return defaultValue;
        }
    }

    public static void set(String key, String value) {
        try {
            template.boundValueOps(key).set(value);
        } catch (Exception e) {
            log.error("redis set error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis set error", e);
        }
    }

    /**
     * 设置失效时间
     *
     * @param key
     * @param value
     * @param timeout seconds
     */
    public static void set(String key, String value, long timeout) {
        try {
            template.boundValueOps(key).set(value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis set error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis set error", e);
        }
    }

    public static void expire(String key, long timeout) {
        try {
            template.boundValueOps(key).expire(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis expire error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis expire error", e);
        }
    }

    public static Long ttl(String key) {
        try {
            return template.boundValueOps(key).getExpire();
        } catch (Exception e) {
            log.error("redis ttl error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis ttl error");
        }
    }

    public static void del(String key) {
        try {
            template.delete(key);
        } catch (Exception e) {
            log.error("redis del error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis del error", e);
        }
    }

    public static void lpush(String key, String value) {
        try {
            template.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("redis lpush error, key: {}, val: {}", key, value, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis lpush error", e);
        }
    }

    public static String rpop(String key) {
        try {
            return template.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("redis rpop error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis rpop error", e);
        }
    }

    public static void ltrim(String key, long start, long end) {
        try {
            template.opsForList().trim(key, start, end);
        } catch (Exception e) {
            log.error("redis ltrim error, key: {}, start: {}, end: {}", key, start, end, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis ltrim error", e);
        }
    }

    public static List<String> lrange(String key, long start, long end) {
        try {
            return template.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("redis lrange error, key: {}, start: {}, end: {}", key, start, end, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis lrange error", e);
        }
    }

    public static Long llen(String key) {
        try {
            return template.opsForList().size(key);
        } catch (Exception e) {
            log.error("redis llen error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis llen error", e);
        }
    }

    public static void sadd(String key, String... values) {
        try {
            BoundSetOperations<String, String> boundSetOps = template.boundSetOps(key);
            boundSetOps.add(values);
        } catch (Exception e) {
            log.error("redis sadd error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis sadd error", e);
        }
    }

    /**
     * 新增值并设置K,V过期时长，单位秒
     *
     * @param key
     * @param value 新增值
     */
    public static void sadd(String key, String value, long timeout) {
        try {
            BoundSetOperations<String, String> boundSetOps = template.boundSetOps(key);
            boundSetOps.add(value);
            boundSetOps.expire(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis sadd error, key: {}, expire: {}", key, timeout, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis sadd error", e);
        }
    }

    /**
     * 移除set中的元素
     *
     * @param key    set名称
     * @param values 元素values
     */
    public static void srem(String key, String... values) {
        try {
            BoundSetOperations<String, String> boundSetOps = template.boundSetOps(key);
            boundSetOps.remove(values);
        } catch (Exception e) {
            log.error("redis srem error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis srem error", e);
        }
    }

    public static Set<String> sdiff(String key1, String key2) {
        try {
            BoundSetOperations<String, String> boundSetOps = template.boundSetOps(key1);
            return boundSetOps.diff(key2);
        } catch (Exception e) {
            log.error("redis sdiff error, key1: {}, key2: {}", key1, key2, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis sdiff error", e);
        }
    }

    public static Set<String> smembers(String key) {
        try {
            BoundSetOperations<String, String> boundSetOps = template.boundSetOps(key);
            return boundSetOps.members();
        } catch (Exception e) {
            log.error("redis smembers error, key: {}", key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis smembers error", e);
        }
    }

    public static void zadd(String key, String value, long score) {
        try {
            template.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("redis zadd error, key: {}, value: {}, score: {}", key, value, score, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis zadd error", e);
        }
    }

    public static Double zscore(String key, String value) {
        try {
            return template.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("redis zscore error, key: {}, value: {}", key, value, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis zscore error", e);
        }
    }

    public static Long zcount(String key, long start, long end) {
        try {
            return template.opsForZSet().count(key, start, end);
        } catch (Exception e) {
            log.error("redis zcount error, key: {}, start: {}, end: {}", key, start, end, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis zcount error", e);
        }
    }

    public static Long zrem(String key, String value) {
        try {
            return template.opsForZSet().remove(key, value);
        } catch (Exception e) {
            log.error("redis zrem error, key: {}, value: {}", key, value, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis zrem error", e);
        }
    }

    public static Long zremrangebyscore(String key, long start, long end) {
        try {
            log.info("将要清除的set集合：{}", template.opsForZSet().rangeByScore(key, start, end));
            return template.opsForZSet().removeRangeByScore(key, start, end);
        } catch (Exception e) {
            log.error("redis zremrangebyscore error, key: {}, start: {}, end: {}", key, start, end, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis zremrangebyscore error", e);
        }
    }

    public static Set<String> zrevrange(String key, long start, long end) {
        try {
            return template.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("redis zrevrange key: {}, start: {}, end: {}", key, start, end, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis zrevrange error", e);
        }
    }

    public static Set<String> zrangebyscore(String key, long start, long end) {
        try {
            return template.opsForZSet().rangeByScore(key, start, end);
        } catch (Exception e) {
            log.error("redis zrangebyscore error, key: {}, start: {}, end: {}", key, start, end, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis zrangebyscore error", e);
        }
    }

    /**
     * 获得hash中的value
     *
     * @param hashName
     * @param key
     * @return
     */
    public static String hget(String hashName, String key) {
        try {
            HashOperations<String, String, String> ops = template.opsForHash();
            return ops.get(hashName, key);
        } catch (Exception e) {
            log.error("redis hget error, hashName: {}, key: {}", hashName, key, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis hget error", e);
        }
    }

    /**
     * 新增hash中的key-value
     *
     * @param hashName hash名称
     * @param key      key
     * @param value    value
     */
    public static void hset(String hashName, String key, String value) {
        try {
            HashOperations<String, String, String> ops = template.opsForHash();
            ops.put(hashName, key, value);
        } catch (Exception e) {
            log.error("redis hset error, hashName: {}, key: {}, value: {}", hashName, key, value, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis hset error", e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void hmset(String hashName, Map map) {
        try {
            template.opsForHash().putAll(hashName, map);
        } catch (Exception e) {
            log.error("redis hmset error, hashName: {}", hashName, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis hmset error", e);
        }
    }

    public static List<Object> hmget(String hashName, List<Object> keys) {
        try {
            return template.opsForHash().multiGet(hashName, keys);
        } catch (Exception e) {
            log.error("redis hmget error, hashName: {}", hashName, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis hmget error", e);
        }
    }

    /**
     * 获得hash中的key-value
     *
     * @param hashName
     * @return
     */
    public static Map<String, String> hgetall(String hashName) {
        try {
            HashOperations<String, String, String> ops = template.opsForHash();
            return ops.entries(hashName);
        } catch (Exception e) {
            log.error("redis hgetall error, hashName: {}", hashName, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis hgetall error", e);
        }
    }

    /**
     * hash的长度
     *
     * @param hashName
     * @return
     */
    public static Long hlen(String hashName) {
        try {
            HashOperations<String, String, String> ops = template.opsForHash();
            return ops.size(hashName);
        } catch (Exception e) {
            log.error("redis hlen error, hashName: {}", hashName, e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis hlen error", e);
        }
    }

    public static void convertAndSend(String channel, String message) {
        try {
            template.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("redis 消息发送异常", e);
            throw new ServiceException(ResponseCode.RDB_ERR, "redis 消息发送异常", e);
        }
    }

    /**
     * 加锁
     *
     * @param lockKey
     * @param seconds
     * @param supplier
     * @return
     */
    public static ResponseBean withRenderLock(String lockKey, long seconds, Supplier<? extends ResponseBean> supplier) {
        String uuid = UUID.randomUUID().toString();
        boolean lockStatus = RedisUtil.tryLock(lockKey, uuid, seconds);
        if (!lockStatus) {
            log.info("{} lock fail", lockKey);
            return new ResponseBean(ResponseCode.LOCK_FAIL, ResponseCode.LOCK_FAIL_CN);
        }
        try {
            return supplier.get();
        } finally {
            RedisUtil.releaseLock(lockKey, uuid);
        }
    }

    /**
     * redis单实例或主备模式分布式锁
     * 对于 Redis 集群则无法使用
     *
     * @param key     加锁键
     * @param value   加锁唯一标识,推荐使用UUID
     * @param seconds 锁过期时间,秒
     * @return
     */
    public static Boolean tryLock(String key, String value, long seconds) {
        try {
            return template.execute((RedisCallback<Boolean>) (connection) ->
                    connection.set(template.getStringSerializer().serialize(key),
                            template.getStringSerializer().serialize(value),
                            Expiration.from(seconds, TimeUnit.SECONDS),
                            RedisStringCommands.SetOption.ifAbsent())
            );
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
    public static Boolean releaseLock(String key, String value) {
        try {
            return template.execute((RedisCallback<Boolean>) redisConnection -> {
                RedisSerializer<String> serializer = template.getStringSerializer();
                Long result = redisConnection.eval(serializer.serialize(RELEASE_LOCK_SCRIPT),
                        ReturnType.INTEGER, 1, serializer.serialize(key), serializer.serialize(value));
                if (RELEASE_SUCCESS.equals(result)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

}
