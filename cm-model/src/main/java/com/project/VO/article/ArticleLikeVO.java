package com.project.VO.article;

import lombok.Data;

import java.util.List;

/**
 * 动态中点赞相关
 */
@Data
public class ArticleLikeVO {
    /**
     * 点赞数
     */
    private Integer count;

    /**
     * 点赞用户
     */
    private List<ArticleUserVO> articleUserVOList;
}
