package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.ArticleVO;
import com.project.domain.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {
    // 根据用户id查询动态
    List<ArticleVO> queryArticleByUserId(Long id);

    // 查询校园动态（不包括关注的用户的动态）
    List<ArticleVO> queryArticle(Long userId);

    // 查询关注用户的动态
    List<ArticleVO> queryArticleByAttention(Long userId);

    // 根据动态id删除动态
    boolean deleteByArticleId(Long articleId);

    // 查询动态数量
    Integer articleCount(Long userId);
}
