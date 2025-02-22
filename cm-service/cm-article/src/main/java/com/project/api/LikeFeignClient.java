package com.project.api;

import com.project.VO.article.ArticleLikeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "likeFeignClient", url = "http://localhost:8085")
public interface LikeFeignClient {
    /**
     * 根据动态id查询点赞信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 点赞数
     * - List<ArticleUserVO> 点赞用户集合
     */
    @GetMapping("/api/likes/queryLikeUser")
    ArticleLikeVO queryLikeInfo(@RequestParam("articleId") Long articleId);
}
