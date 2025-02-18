package com.project.controller;

import com.project.VO.UserVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.UserService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

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

}
