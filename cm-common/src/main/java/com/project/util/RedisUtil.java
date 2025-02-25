package com.project.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis 的相关操作工具类
 */
@Component
@Slf4j
public class RedisUtil {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 开启事务，进行 Redis 操作
     *
     * @param key 操作的 Redis 数据前缀
     */
    public void redisTransaction(String key) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) {
                try {
                    operations.multi(); // 开始事务
                    operations.delete(key); // 要执行的操作
                    return operations.exec(); // 提交事务
                } catch (Exception e) {
                    operations.discard(); // 取消事务
                    log.error("Redis 发生异常，删除缓存失败", e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Redis 存储数据
     *
     * @param key  操作的 Redis 数据前缀
     * @param data 具体数据
     */
    public void setRedisData(String key, String data) {
        redisTemplate.opsForValue().set(key, data, 5, TimeUnit.MINUTES);
    }

    /**
     * Redis 获取数据
     *
     * @param key 操作的 Redis 数据前缀
     * @return 被序列化的数据
     */
    public String getRedisData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
