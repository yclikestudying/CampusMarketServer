package com.project.VO.article;

import lombok.Data;

import java.util.Date;

/**
 * 动态相关
 */
@Data
public class ArticleVO {
    // 动态相关
    /**
     * 动态id
     */
    private Long articleId;

    /**
     * 动态内容
     */
    private String articleContent;

    /**
     * 动态图片
     */
    private String articlePhotos;

    /**
     * 动态发布时间
     */
    private Date createTime;

    // 用户相关
    private ArticleUserVO publishUser;

    // 点赞相关
    private ArticleLikeVO like;

    // 评论相关
    private ArticleCommentVO comment;
}
