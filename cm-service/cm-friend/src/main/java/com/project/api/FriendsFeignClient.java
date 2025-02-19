package com.project.api;

import com.project.VO.FriendVO;
import com.project.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "friendsFeignClient", url = "http://localhost:8082")
public interface FriendsFeignClient {
    /**
     * 批量查询用户
     * @param ids
     * @return
     */
    @PostMapping("/user/getUserInfo")
    List<FriendVO> getUserInfo(@RequestBody List<Long> ids);
}
