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
@RequestMapping("/friend")
@Slf4j
public class FriendAPI {
    @Resource
    private FriendsService friendsService;

    /**
     * 查询我的关注api
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/attentionApi")
    public List<FriendVO> attentionApi(@RequestParam(value = "userId", required = false) Long userId) {
        return friendsService.attention(userId);
    }
}
