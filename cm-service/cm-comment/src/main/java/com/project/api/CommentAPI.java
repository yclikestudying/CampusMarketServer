package com.project.api;

import com.project.VO.article.ArticleCommentVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentAPI {
    @Resource
    private CommentService commentService;

    /**
     * 根据动态id查询评论信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 评论数
     * - List<ArticleUserVO> 评论用户
     */
    @GetMapping("/queryCommentInfo")
    public ArticleCommentVO queryCommentInfo(@RequestParam("articleId") Long articleId) {
        return commentService.queryCommentInfo(articleId);
    }

    /**
     * 根据动态id删除评论信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - int 删除的行数
     */
    @GetMapping("/deleteCommentInfo")
    public int deleteCommentInfo(@RequestParam("articleId") Long articleId) {
        return commentService.deleteCommentInfo(articleId);
    }
}
