package com.project.constants;

public class RedisKeyConstants {

    // 用户相关 Key
    public static final String USER_TOKEN_PREFIX = "user:token:"; // 用户 Token 前缀
    public static final String USER_INFO_PREFIX = "user:info:";   // 用户信息前缀
    public static final String USER_ATTENTION = "user:attention:"; // 我的关注用户前缀
    public static final String USER_FANS = "user:fans:"; // 我的粉丝前缀
    public static final String USER_ATTENTION_AND_FANS = "user:attention_and_fans:"; // 互关用户前缀
    public static final String USER_ARTICLE = "user:article:"; // 用户动态前缀

    // 生成用户 Token 的 Key
    public static String getUserTokenKey(Long userId) {
        return USER_TOKEN_PREFIX + userId;
    }

    // 生成用户信息的 Key
    public static String getUserInfoKey(Long userId) {
        return USER_INFO_PREFIX + userId;
    }
    // 生成我的关注用户的 key
    public static String getUserAttentionKey(Long userId) {
        return USER_ATTENTION + userId;
    }
    // 生成我的粉丝的 Key
    public static String getUserFansKey(Long userId) {
        return USER_FANS + userId;
    }
    // 生成互关用户的 Key
    public static String getUserAttentionAndFansKey(Long userId) {
        return USER_ATTENTION_AND_FANS + userId;
    }
    // 生成用户动态的 Key
    public static String getUserArticleKey(Long userId) {
        return USER_ARTICLE + userId;
    }
}
