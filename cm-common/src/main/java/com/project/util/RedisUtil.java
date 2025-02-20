package com.project.util;

import com.project.constants.RedisKeyConstants;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisUtil {
    private static final RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

    /**
     * 存储数据
     *
     * @param str
     * @param userId
     * @param data
     */
    public static void saveIntoRedis(String str, Long userId, String data) {
        String redisKey = RedisKeyConstants.getRedisKey(str, userId);
        redisTemplate.opsForValue().set(redisKey, data);
    }

    /**
     * 获取数据
     *
     * @param str
     * @param userId
     * @return
     */
    public static String getFromRedis(String str, Long userId) {
        String redisKey = RedisKeyConstants.getRedisKey(str, userId);
        return redisTemplate.opsForValue().get(redisKey);
    }
}
