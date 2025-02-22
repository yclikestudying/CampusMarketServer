package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.article.ArticleLikeVO;
import com.project.domain.Likes;
import java.util.Map;

public interface LikesService extends IService<Likes> {
    /**
     * 根据动态id查询点赞信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 点赞数
     * - List<ArticleUserVO> 点赞用户集合
     */
    ArticleLikeVO queryLikeInfo(Long articleId);
    // 根据动态id进行点赞
    String like(Long userId, Long articleId);
    // 根据动态id取消点赞
    String unlike(Long userId, Long articleId);
}
