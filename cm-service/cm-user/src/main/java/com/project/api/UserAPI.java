package com.project.api;

import com.project.VO.FriendVO;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserAPI {
    @Resource
    private UserService userService;

    /**
     * 根据用户id查询用户信息
     */
    @GetMapping("/getUserInfoByUserIdApi")
    public UserVO getUserInfoByUserIdApi(@RequestParam(value = "userId") Long userId) {
        return userService.getUserInfoByUserId(userId);
    }

    /**
     * 批量获取用户信息
     */
    @PostMapping("/getUserInfoApi")
    public List<FriendVO> getUserInfoApi(@RequestBody List<Long> ids) {
        return userService.getUserInfo(ids);
    }
}
