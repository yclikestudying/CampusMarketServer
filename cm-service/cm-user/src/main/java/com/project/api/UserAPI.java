package com.project.api;

import com.project.VO.FriendVO;
import com.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserAPI {
    @Resource
    private UserService userService;

    /**
     * 批量获取用户信息
     */
    @PostMapping("/getUserInfoApi")
    public List<FriendVO> getUserInfoApi(@RequestBody List<Long> ids) {
        return userService.getUserInfo(ids);
    }
}
