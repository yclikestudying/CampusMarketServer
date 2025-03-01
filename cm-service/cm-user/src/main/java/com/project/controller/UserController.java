package com.project.controller;

import com.project.VO.FriendVO;
import com.project.VO.user.UserVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.service.UserService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/userInfo")
@Api(tags = "用户模块")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 查询用户信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - userVO 用户信息
     */
    @GetMapping("/getUserInfoByUserId")
    @ApiOperation(value = "查询用户完整信息")
    public Result<UserVO> getUserInfoByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        log.info("查询用户完整信息, 参数:{}", userId);
        UserVO uservo = userService.getUserInfoByUserId(userId);
        return Result.success(Objects.requireNonNull(ResultCodeEnum.getByCode(200)), uservo);
    }

    /**
     * 修改用户个人信息
     * 请求数据:
     * - key 键值
     * - value 值
     */
    @PutMapping("/updateUser")
    @ApiOperation(value = "修改用户个人信息")
    public Result<String> updateUserInfo(@RequestBody Map<String, Object> map) {
        boolean result = userService.updateUser(UserContext.getUserId(), map);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 修改用户个人头像
     * 请求数据:
     * - file 图片二进制数据
     */
    @PostMapping("/updateAvatar")
    @ApiOperation(value = "修改用户个人头像")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        boolean result = userService.updateAvatar(UserContext.getUserId(), file);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 模糊搜索用户
     * 请求数据
     * - keyword 用户名
     * 响应数据
     * - List<FriendVO> 用户集合
     */
    @GetMapping("/queryLikeUser")
    @ApiOperation(value = "模糊搜索用户")
    public Result<List<FriendVO>> queryLikeUser(@RequestParam(value = "keyword", required = false) String keyword) {
        List<FriendVO> list = userService.queryLikeUser(keyword);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
