package com.project.api;

import com.project.VO.article.ArticleLikeVO;
import com.project.domain.Article;
import com.project.service.LikesService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/likes")
@Slf4j
public class LikeAPI {
    @Resource
    private LikesService likesService;

    /**
     * 根据动态id查询点赞信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 点赞数
     * - List<ArticleUserVO> 点赞用户集合
     */
    @GetMapping("/queryLikeUser")
    public ArticleLikeVO queryLikeInfo(@RequestParam("articleId") Long articleId) {
        return likesService.queryLikeInfo(articleId);
    }
}
