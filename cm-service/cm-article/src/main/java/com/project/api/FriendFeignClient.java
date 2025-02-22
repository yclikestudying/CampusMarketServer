package com.project.api;

import com.project.VO.FriendVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient(name = "friendFeignClient", url = "http://localhost:8083")
public interface FriendFeignClient {
    /**
     * 查询我的关注
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    @GetMapping("/api/friend/attention")
    List<FriendVO> attention(@RequestParam(value = "userId", required = false) Long userId);
}
