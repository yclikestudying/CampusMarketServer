package com.project.controller;

import com.project.VO.FriendVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.FriendsService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    @GetMapping("/attention")
    @ApiOperation(value = "查询关注用户")
    public Result<List<FriendVO>> attention(@RequestParam(value = "userId", required = false) Long userId) {
        List<FriendVO> attention = friendsService.attention(userId);
        return Result.success(ResultCodeEnum.SUCCESS, attention);
    }

    /**
     * 查询我的粉丝
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
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
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    @GetMapping("/attentionAndFans")
    @ApiOperation(value = "查询互关用户")
    public Result<List<FriendVO>> attentionAndFans(@RequestParam(value = "userId", required = false) Long userId) {
        log.info("查询互关用户，参数:{}", userId);
        List<FriendVO> list = friendsService.attentionAndFans(userId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

//    /**
//     * 查询我的关注数量
//     * 请求数据:
//     * - userId 用户id
//     * 响应数据
//     * - count 关注数量
//     */
//    @GetMapping("/attentionCount")
//    @ApiOperation(value = "查询我的关注数量")
//    public Result<Integer> attentionCount(@RequestParam(value = "userId", required = false) Long userId) {
//        log.info("查询我的关注数量");
//        Integer count = friendsService.attentionCount(userId);
//        return Result.success(ResultCodeEnum.SUCCESS, count);
//    }
//
//    /**
//     * 查询我的粉丝数量
//     * 请求数据:
//     * - userId 用户id
//     * 响应数据
//     * - count 粉丝数量
//     */
//    @GetMapping("/fansCount")
//    @ApiOperation(value = "查询我的粉丝数量")
//    public Result<Integer> fansCount(@RequestParam(value = "userId", required = false) Long userId) {
//        log.info("查询我的粉丝数量");
//        Integer count = friendsService.fansCount(userId);
//        return Result.success(ResultCodeEnum.SUCCESS, count);
//    }
//
//    /**
//     * 查询互关用户数量
//     * 请求数据:
//     * - userId 用户id
//     * 响应数据
//     * - count 互关用户数量
//     */
//    @GetMapping("/attentionAndFansCount")
//    @ApiOperation(value = "查询互关用户数量")
//    public Result<Integer> attentionAndFansCount() {
//        log.info("查询互关用户数量");
//        Integer count = friendsService.attentionAndFansCount(UserContext.getUserId());
//        return Result.success(ResultCodeEnum.SUCCESS, count);
//    }

    /**
     * 关注用户
     * 请求数据
     * - userId 我的id
     * - otherId 关注用户的id
     */
    @PutMapping("/attentionUser")
    @ApiOperation(value = "关注用户")
    public Result<String> attentionUser(@RequestParam("userId") Long userId, @RequestParam("otherId") Long otherId) {
        boolean result = friendsService.attentionUser(userId, otherId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 取消关注用户
     * 请求数据
     * - userId 我的id
     * - otherId 关注用户的id
     */
    @PutMapping("/unAttentionUser")
    @ApiOperation(value = "关注用户")
    public Result<String> unAttentionUser(@RequestParam("userId") Long userId, @RequestParam("otherId") Long otherId) {
        boolean result = friendsService.unAttentionUser(userId, otherId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }
}
