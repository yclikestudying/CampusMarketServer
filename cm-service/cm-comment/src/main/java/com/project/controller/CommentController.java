package com.project.controller;

import com.project.VO.CommentVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论模块")
@Slf4j
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 根据动态id查询评论
     *
     * @param articleId
     * @return
     */
    @GetMapping("/queryCommentByArticleId")
    @ApiOperation(value = "根据动态id查询评论")
    public Result<List<CommentVO>> queryCommentByArticleId(@RequestParam("articleId") Long articleId) {
        List<CommentVO> commentVOList = commentService.queryCommentByArticleId(articleId);
        return Result.success(ResultCodeEnum.SUCCESS, commentVOList);
    }
}
