package com.project.api;

import com.project.VO.article.ArticleCommentVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commentFeignClient", url = "http://localhost:8086")
public interface CommentFeignClient {
    /**
     * 根据动态id查询评论信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 评论数
     * - List<ArticleUserVO> 评论用户
     */
    @GetMapping("/api/comment/queryCommentInfo")
    ArticleCommentVO queryCommentInfo(@RequestParam("articleId") Long articleId);
}
