package com.project.api;

import com.project.VO.FriendVO;
import com.project.VO.article.ArticleUserVO;
import com.project.VO.user.UserInfoVO;
import com.project.domain.User;
import com.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/userInfo")
@Slf4j
public class UserAPI {
    @Resource
    private UserService userService;

    /**
     * 查询用户信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - userId 用户id
     * - userName 用户名称
     * - userAvatar 用户头像
     */
    @GetMapping("/getUserInfo")
    public UserInfoVO getUserInfo(@RequestParam("userId") Long userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 批量查询用户信息（动态相关的用户查询）
     * 请求数据:
     * - List<Long> 用户id集合
     * 响应数据:
     * - List<ArticleUserVO> 用户信息集合
     */
    @PostMapping("/getUserInfoBatch")
    public List<ArticleUserVO> getUserInfoBatch(@RequestBody List<Long> userIds) {
        return userService.getUserInfoBatch(userIds);
    }

    /**
     * 批量查询用户信息（关注相关的用户查询）
     * 请求数据:
     * - List<Long> 用户id集合
     * 响应数据:
     * - List<FriendVO> 用户信息集合
     */
    @PostMapping("/getUserBatch")
    public List<FriendVO> getUserBatch(@RequestBody List<Long> userIds) {
        return userService.getUserBatch(userIds);
    }
}
