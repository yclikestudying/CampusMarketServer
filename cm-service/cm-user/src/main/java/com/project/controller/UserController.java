package com.project.controller;

import com.project.VO.FriendVO;
import com.project.VO.UserVO;
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
@RequestMapping("/user")
@Api(tags = "用户模块")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 根据用户id查询用户信息
     */
    @GetMapping("/getUserInfoByUserId")
    @ApiOperation(value = "根据用户id查询用户信息")
    public Result<UserVO> getUserInfoByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        log.info("根据用户id查询用户信息, 参数:{}", userId);
        Long id = userId == null ? UserContext.getUserId() : userId;
        UserVO uservo = userService.getUserInfoByUserId(id);
        return Result.success(Objects.requireNonNull(ResultCodeEnum.getByCode(200)), uservo);
    }

    /**
     * 修改用户个人信息
     *
     * @param map   用户修改的信息数据
     * @return success or fail
     */
    @PutMapping("/updateUser")
    @ApiOperation(value = "修改用户个人信息")
    public Result<String> updateUserInfo(@RequestBody Map<String, Object> map) {
        boolean result = userService.updateUser(UserContext.getUserId(), map);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 修改用户头像
     *
     * @param file  文件
     * @return success or fail
     */
    @PostMapping("/updateAvatar")
    @ApiOperation(value = "修改用户头像")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        boolean result = userService.updateAvatar(UserContext.getUserId(), file);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 用户退出登录
     */
    @DeleteMapping("/logout")
    @ApiOperation(value = "用户退出")
    public void logout() {
        redisTemplate.delete(RedisKeyConstants.getUserTokenKey(UserContext.getUserId()));
    }

    /**
     * 批量获取用户信息
     * 提供给其他模块调用
     */
    @PostMapping("/getUserInfoApi")
    @ApiOperation(value = "批量获取用户信息API")
    public List<FriendVO> getUserInfoApi(@RequestBody List<Long> ids) {
        return userService.getUserInfo(ids);
    }
}
