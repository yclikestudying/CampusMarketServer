package com.project.controller;


import com.project.DTO.PhoneLoginDTO;
import com.project.DTO.PhoneRegisterDTO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 用户手机登录
     *
     * @param phoneLoginDTO 登录信息
     * @return
     */
    @PostMapping("/phoneLogin")
    @ApiOperation(value = "用户手机登录")
    public Result<Map<String, Object>> login(@RequestBody PhoneLoginDTO phoneLoginDTO) {
        log.info("用户登录，请求参数:{}", phoneLoginDTO);
        Map<String, Object> map = loginService.phoneLogin(phoneLoginDTO);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 用户手机注册
     *
     * @param phoneRegisterDTO 注册信息
     * @return
     */
    @PostMapping("/phoneRegister")
    @ApiOperation(value = "用户手机注册")
    public Result<String> register(@RequestBody PhoneRegisterDTO phoneRegisterDTO) {
        log.info("用户注册，请求参数:{}", phoneRegisterDTO);
        boolean result = loginService.phoneRegister(phoneRegisterDTO);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.success(ResultCodeEnum.FAIL);
    }
}
