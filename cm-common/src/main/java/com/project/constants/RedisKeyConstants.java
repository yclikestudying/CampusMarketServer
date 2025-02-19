package com.project.constants;

public class RedisKeyConstants {

    // 用户相关 Key
    public static final String USER_TOKEN_PREFIX = "user:token:"; // 用户 Token 前缀
    public static final String USER_INFO_PREFIX = "user:info:";   // 用户信息前缀

    // 示例：生成用户 Token 的 Key
    public static String getUserTokenKey(Long userId) {
        return USER_TOKEN_PREFIX + userId;
    }

    // 示例：生成用户信息的 Key
    public static String getUserInfoKey(Long userId) {
        return USER_INFO_PREFIX + userId;
    }
}
