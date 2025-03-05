package com.project.constants;

public class RedisKeyConstants {

    // 项目名称 key
    public static final String PROJECT_NAME = "CampusMarket:"; // 项目名称 key
    // 用户相关 key
    public static final String USER_TOKEN = "user:token:"; // 用户 token key
    public static final String USER_INFO = "user:info:"; // 用户个人信息 key
    public static final String USER_ATTENTION = "user:attention:"; // 我的关注 key
    public static final String USER_FANS = "user:fans:"; // 我的粉丝 key
    public static final String USER_ATTENTIONFANS = "user:attentionfans:"; // 互关 key
    public static final String USER_VISIT = "user:visit:"; // 我的访客 key
    // 动态相关
    public static final String ARTICLE_USER = "article:user:"; // 用户动态 key
    public static final String ARTICLE_ATTENTION = "article:attention:"; // 关注用户动态 key
    public static final String ARTICLE_SCHOOL = "article:school:"; // 校园动态 key
    // 消息相关
    public static final String MESSAGE_LIST = "message:list:"; // 用户消息列表 key
    public static final String MESSAGE_ARRAY = "message:array:"; // 消息数组 key
    // 管理员相关
    public static final String ADMIN_INFO = "admin:info:"; // 管理员信息 key
    public static final String ADMIN_TOKEN = "admin:token:"; // 管理员token key

    public static String getRedisKey(String key, Long userId) {
        return PROJECT_NAME + key + userId;
    }
}
