package com.project.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "friendFeignClient", url = "http://localhost:8083")
public interface FriendFeignClient {
    /**
     * 查询某人是否是我的关注
     * 请求数据:
     * - userId 用户id
     * - otherId 被评论者id
     * 响应数据
     * - true or false
     */
    @GetMapping("/api/friend/isAttention")
    boolean isAttention(@RequestParam("userId") Long userId, @RequestParam("otherId") Long otherId);
}
