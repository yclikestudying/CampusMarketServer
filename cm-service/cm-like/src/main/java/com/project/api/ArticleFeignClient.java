package com.project.api;

import com.project.VO.article.ArticleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "articleFeignClient", url = "http://localhost:8084")
public interface ArticleFeignClient {
    /**
     * 根据用户id查询动态API
     */
    @GetMapping("/article/queryArticleByUserIdApi")
    List<ArticleVO> queryArticleByUserIdApi(@RequestParam(value = "userId", required = false) Long userId);

    /**
     * 查询校园动态API（不包括关注的用户的动态）
     */
    @GetMapping("/article/queryArticleApi")
    List<ArticleVO> queryArticleApi();

    /**
     * 查询关注用户的动态API
     */
    @GetMapping("/article/queryArticleByAttentionApi")
    List<ArticleVO> queryArticleByAttentionApi();
}