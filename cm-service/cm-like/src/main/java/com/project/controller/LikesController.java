package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.LikesService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@Api(tags = "文章点赞模块")
@Slf4j
public class LikesController {
    @Resource
    private LikesService likesService;

    /**
     * 根据动态id进行点赞
     * 请求数据
     * - articleId 动态id
     * - userId 被点赞用户id
     * 响应数据
     * - success 点赞成功
     */
    @PutMapping("/like")
    @ApiOperation(value = "根据动态id进行点赞")
    public Result<String> like(@RequestParam("articleId") Long articleId, @RequestParam("userId") Long userId) {
        String str = likesService.like(articleId, userId);
        return Result.success(ResultCodeEnum.SUCCESS, str);
    }

    /**
     * 根据动态id取消点赞
     * 请求数据
     * - articleId 动态id
     * - userId 用户id
     * 响应数据
     * - success 取消点赞成功
     */
    @PutMapping("/unlike")
    @ApiOperation(value = "根据动态id取消点赞")
    public Result<String> unlike(@RequestParam("articleId") Long articleId, @RequestParam("userId") Long userId) {
        String str = likesService.unlike(articleId, userId);
        return Result.success(ResultCodeEnum.SUCCESS, str);
    }
}
