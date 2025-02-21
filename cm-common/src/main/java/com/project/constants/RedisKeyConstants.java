package com.project.constants;

public class RedisKeyConstants {

    // 用户相关 Key
    public static final String USER_TOKEN_PREFIX = "user:token:"; // 用户 Token 前缀
    public static final String USER_INFO_PREFIX = "user:info:";   // 用户信息前缀
    public static final String USER_ATTENTION = "user:attention:"; // 我的关注用户前缀
    public static final String USER_FANS = "user:fans:"; // 我的粉丝前缀
    public static final String USER_ATTENTION_AND_FANS = "user:attention_and_fans:"; // 互关用户前缀
    public static final String ARTICLE_USER = "article:user:"; // 用户动态前缀
    public static final String ARTICLE_SCHOOL = "article:school:"; // 校园动态前缀
    public static final String ARTICLE_ATTENTION = "article:attention:"; // 关注用户动态前缀
    public static final String LIKE_USER = "like:user:"; // 用户自己的文章点赞前缀
    public static final String LIKE_SCHOOL = "like:school:"; // 校园动态点赞前缀
    public static final String LIKE_ATTENTION = "like:attention:"; // 关注用户动态的点赞前缀
    public static final String ARTICLE_COMMENT = "article:comment:"; // 动态评论前缀
    public static final String ARTICLE_COUNT = "article:count:"; // 动态数量前缀
    public static final String ATTENTION_COUNT = "attention:count:"; // 关注数量前缀
    public static final String FANS_COUNT = "fans:count:"; // 粉丝数量前缀
    public static final String ATTENTIONFANS_COUNT = "attentionfans:count:"; // 互关数量前缀

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
    public static String getArticleUserKey(Long userId) {
        return ARTICLE_USER + userId;
    }
    // 生成校园动态的 Key
    public static String getArticleSchoolKey(Long userId) {
        return ARTICLE_SCHOOL + userId;
    }
    // 生成关注用户动态的 Key
    public static String getArticleAttentionKey(Long userId) {return ARTICLE_ATTENTION + userId;}
    // 生成用户自己的文章点赞的 Key
    public static String getLikeUserKey(Long userId) {return LIKE_USER + userId;}
    // 生成校园动态点赞的 Key
    public static String getLikeSchoolKey(Long userId) {return LIKE_SCHOOL + userId;}

    public static String getRedisKey(String str, Long userId) {
        return str + userId;
    }
}
