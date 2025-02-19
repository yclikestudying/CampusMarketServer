package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.ArticleVO;
import com.project.domain.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {
    // 根据用户id查询动态
    List<ArticleVO> queryArticleByUserId(Long id);
}
