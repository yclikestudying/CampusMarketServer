package com.project.api;

import com.project.VO.article.ArticleUserVO;
import com.project.VO.user.UserInfoVO;
import com.project.VO.user.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "userFeignClient", url = "http://localhost:8082")
public interface UserFeignClient {
    /**
     * 查询用户信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - userId 用户id
     * - userName 用户名称
     * - userAvatar 用户头像
     */
    @GetMapping("/api/userInfo/getUserInfo")
    UserInfoVO getUserInfo(@RequestParam("userId") Long userId);
}
