package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.article.ArticleVO;
import com.project.domain.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {
    /**
     * 查询用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    List<ArticleVO> queryArticleByUserId(Long id);

    /**
     * 查询关注用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    List<ArticleVO> queryArticleOfAttention(Long userId);

    /**
     * 查询校园所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    List<ArticleVO> queryArticleOfSchool(Long userId);
}
