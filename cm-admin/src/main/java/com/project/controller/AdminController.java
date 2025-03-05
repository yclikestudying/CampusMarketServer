package com.project.controller;

import com.project.DTO.LoginDTO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.service.AdminService;
import com.project.util.RedisUtil;
import com.project.util.UserContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminService adminService;
    @Resource
    private RedisUtil redisUtil;

    /**
     * 管理员登录
     * 请求数据
     * - userPhone 手机号
     * - userPassword 密码
     */
    @PostMapping("/login")
    @ApiOperation(value = "管理员登录")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> map = adminService.login(loginDTO);
        return Result.success(ResultCodeEnum.SUCCESS, map);
    }

    /**
     * 管理员退出
     */
    @DeleteMapping("/logout")
    @ApiOperation(value = "管理员退出")
    public Result<String> logout() {
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ADMIN_TOKEN, UserContext.getUserId());
        redisUtil.redisTransaction(redisKey);
        return Result.success(ResultCodeEnum.SUCCESS);
    }
}
