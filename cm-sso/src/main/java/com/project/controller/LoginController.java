package com.project.controller;


import com.project.DTO.PhoneDTO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.service.LoginService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "登录注册模块")
@Slf4j
public class LoginController {
    @Resource
    private LoginService loginService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 手机登录
     * 请求数据:
     * - phone 手机号
     * - password 密码
     * 响应数据:
     * - token 身份标识
     * - user 用户完整信息
     */
    @PostMapping("/phoneLogin")
    @ApiOperation(value = "手机登录")
    public Result<Map<String, Object>> login(@RequestBody PhoneDTO phoneDTO) {
        log.info("手机登录");
        Map<String, Object> map = loginService.phoneLogin(phoneDTO);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 手机注册
     * 请求数据:
     * - phone 手机号
     * - password 密码
     * - checkPassword 校验密码
     */
    @PostMapping("/phoneRegister")
    @ApiOperation(value = "用户手机注册")
    public Result<String> register(@RequestBody PhoneDTO phoneDTO) {
        log.info("手机注册");
        boolean result = loginService.phoneRegister(phoneDTO);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.success(ResultCodeEnum.FAIL);
    }

    /**
     * 用户退出登录
     */
    @DeleteMapping("/logout")
    @ApiOperation(value = "用户退出")
    public Result<String> logout() {
        String tokenKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_TOKEN, UserContext.getUserId());
        redisTemplate.delete(tokenKey);
        return Result.success(ResultCodeEnum.SUCCESS);
    }
}
