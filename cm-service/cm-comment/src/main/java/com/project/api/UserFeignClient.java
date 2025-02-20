package com.project.api;

import com.project.VO.FriendVO;
import com.project.VO.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "userFeignClient", url = "http://localhost:8082")
public interface UserFeignClient {
    /**
     * 根据用户id查询用户信息
     */
    @GetMapping("/user/getUserInfoByUserIdApi")
    UserVO getUserInfoByUserIdApi(@RequestParam(value = "userId") Long userId);

    /**
     * 批量获取用户信息
     */
    @PostMapping("/user/getUserInfoApi")
    List<FriendVO> getUserInfoApi(@RequestBody List<Long> ids);
}
