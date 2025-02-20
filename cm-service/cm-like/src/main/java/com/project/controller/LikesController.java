package com.project.controller;

import com.project.VO.LikeVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.LikesService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 获取自己的动态的点赞
     */
    @GetMapping("/queryLikesOfUser")
    @ApiOperation(value = "获取自己的动态的点赞")
    public Result<Map<Long, Integer>> queryLikesOfUser(@RequestParam(value = "userId", required = false) Long id) {
        Map<Long, Integer> map = likesService.queryLikesOfUser(id);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 获取校园动态的点赞
     */
    @GetMapping("/queryLikesOfCampus")
    @ApiOperation(value = "获取校园动态的点赞")
    public Result<Map<Long, Integer>> queryLikesOfCampus() {
        Map<Long, Integer> map = likesService.queryLikesOfCampus(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 获取关注用户动态的点赞
     */
    @GetMapping("/queryLikesOfAttention")
    @ApiOperation(value = "获取关注用户动态的点赞")
    public Result<Map<Long, Integer>> queryLikesOfAttention() {
        Map<Long, Integer> map = likesService.queryLikesOfAttention(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }
}
