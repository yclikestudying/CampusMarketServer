package com.project.controller;

import com.project.DTO.CommentDTO;
import com.project.VO.CommentVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论模块")
@Slf4j
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 发表评论
     * 请求数据
     * - articleId 动态id
     * - comment 评论内容
     */
    @PostMapping("/publish")
    @ApiOperation(value = "发表评论")
    public Result<String> publish(@RequestBody CommentDTO commentDTO) {
        boolean result = commentService.publish(commentDTO);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 删除评论
     * 请求数据
     * - articleId 动态id
     * - commentId 评论id
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除评论")
    public Result<String> delete(@RequestParam("articleId") Long articleId, @RequestParam("commentId") Long commentId, @RequestParam("userId") Long userId) {
        boolean result = commentService.delete(articleId, commentId, userId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }
}
