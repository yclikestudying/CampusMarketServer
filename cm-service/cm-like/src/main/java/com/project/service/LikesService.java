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

    /**
     * 根据动态id进行点赞
     * 请求数据
     * - articleId 动态id
     * - userId 被点赞用户id
     * 响应数据
     * - success 点赞成功
     */
    String like(Long articleId, Long userId);

    /**
     * 根据动态id取消点赞
     * 请求数据
     * - articleId 动态id
     * - userId 用户id
     * 响应数据
     * - success 取消点赞成功
     */
    String unlike(Long articleId, Long userId);

    /**
     * 根据动态id删除点赞信息
     * 请求数据:
     * - articleId 动态id
     */
    int deleteLikeInfo(Long articleId);
}
