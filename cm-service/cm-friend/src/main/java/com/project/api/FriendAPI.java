package com.project.api;

import com.project.VO.FriendVO;
import com.project.service.FriendsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/friend")
@Slf4j
public class FriendAPI {
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
    public List<FriendVO> attention(@RequestParam(value = "userId", required = false) Long userId) {
        return friendsService.attention(userId);
    }

    /**
     * 查询校园用户
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    @GetMapping("/school")
    public List<FriendVO> school(@RequestParam(value = "userId", required = false) Long userId) {
        return friendsService.school(userId);
    }
}
