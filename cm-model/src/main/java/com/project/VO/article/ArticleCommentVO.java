package com.project.VO.article;

import lombok.Data;

import java.util.List;

/**
 * 动态中评论相关
 */
@Data
public class ArticleCommentVO {
    /**
     * 评论数
     */
    private Integer count;

    /**
     * 评论内容
     */
    private List<String> commentList;

    /**
     * 评论用户
     */
    private List<ArticleUserVO> articleUserVOList;
}
