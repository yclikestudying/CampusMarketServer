package com.project.util;

import com.project.constants.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 存储数据
     *
     * @param str
     * @param userId
     * @param data
     */
    public void saveIntoRedis(String str, Long userId, String data) {
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
    public String getFromRedis(String str, Long userId) {
        String redisKey = RedisKeyConstants.getRedisKey(str, userId);
        return redisTemplate.opsForValue().get(redisKey);
    }
}
