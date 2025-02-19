package com.project.interceptor;

import com.project.constants.RedisKeyConstants;
import com.project.util.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class FeignTokenInterceptor implements RequestInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void apply(RequestTemplate template) {
        String token = redisTemplate.opsForValue().get(RedisKeyConstants.getUserTokenKey(UserContext.getUserId()));
        if (token != null) {
            template.header("Authorization", token); // 将Token添加到请求头中
        }
    }
}
