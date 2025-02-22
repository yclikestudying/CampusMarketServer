package com.project.constants;

public class RedisKeyConstants {

    // 项目名称 key
    public static final String PROJECT_NAME = "CampusMarket:"; // 项目名称 key
    // 用户相关 key
    public static final String USER_TOKEN = "user:token:"; // 用户 token key
    public static final String USER_INFO = "user:info:"; // 用户个人信息 key
    public static final String USER_ATTENTION = "user:attention:"; // 我的关注 key
    // 动态相关
    public static final String ARTICLE_USER = "article:user:"; // 用户动态 key
    public static final String ARTICLE_ATTENTION = "article:attention:"; // 关注用户动态 key
    public static final String ARTICLE_SCHOOL = "article:school:"; // 校园动态 key

    public static final String USER_FANS = "user:fans:"; // 我的粉丝前缀
    public static final String USER_ATTENTION_AND_FANS = "user:attention_and_fans:"; // 互关用户前缀
    public static final String ATTENTION_COUNT = "attention:count:"; // 关注数量前缀
    public static final String FANS_COUNT = "fans:count:"; // 粉丝数量前缀
    public static final String ATTENTIONFANS_COUNT = "attentionfans:count:"; // 互关数量前缀


    // 生成我的粉丝的 Key
    public static String getUserFansKey(Long userId) {
        return USER_FANS + userId;
    }
    // 生成互关用户的 Key
    public static String getUserAttentionAndFansKey(Long userId) {
        return USER_ATTENTION_AND_FANS + userId;
    }
    // 生成用户动态的 Key
    public static String getArticleUserKey(Long userId) {
        return ARTICLE_USER + userId;
    }
    // 生成校园动态的 Key
    public static String getArticleSchoolKey(Long userId) {
        return ARTICLE_SCHOOL + userId;
    }
    // 生成关注用户动态的 Key
    public static String getArticleAttentionKey(Long userId) {return ARTICLE_ATTENTION + userId;}

    public static String getRedisKey(String key, Long userId) {
        return PROJECT_NAME + key + userId;
    }
}
