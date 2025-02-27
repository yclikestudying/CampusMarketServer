package com.project.VO.article;

import lombok.Data;

import java.util.Date;

/**
 * 动态中用户相关
 */
@Data
public class ArticleUserVO {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;
}
