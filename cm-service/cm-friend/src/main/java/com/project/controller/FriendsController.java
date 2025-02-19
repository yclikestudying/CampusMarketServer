package com.project.controller;

import com.project.VO.FriendVO;
import com.project.VO.UserVO;
import com.project.api.FriendsFeignClient;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.service.FriendsService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/friend")
@Api(tags = "好友模块")
@Slf4j
public class FriendsController {
    @Resource
    private FriendsService friendsService;

    /**
     * 查询我的关注
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/attention")
    @ApiOperation(value = "查询关注用户")
    public Result<List<FriendVO>> attention(@RequestParam(value = "userId", required = false) Long userId) {
        log.info("查询我的关注，参数:{}", userId);
        List<FriendVO> attention = friendsService.attention(userId);
        return Result.success(ResultCodeEnum.SUCCESS, attention);
    }

    /**
     * 查询我的粉丝
     */
    @GetMapping("/fans")
    @ApiOperation(value = "查询我的粉丝")
    public Result<List<FriendVO>> fans(@RequestParam(value = "userId", required = false) Long userId) {
        log.info("查询我的粉丝，参数:{}", userId);
        List<FriendVO> fans = friendsService.fans(userId);
        return Result.success(ResultCodeEnum.SUCCESS, fans);
    }

    /**
     * 查询互关用户
     */
    @GetMapping("/attentionAndFans")
    @ApiOperation(value = "查询互关用户")
    public Result<List<FriendVO>> attentionAndFans(@RequestParam(value = "userId", required = false) Long userId) {
        log.info("查询互关用户，参数:{}", userId);
        List<FriendVO> list = friendsService.attentionAndFans(userId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
