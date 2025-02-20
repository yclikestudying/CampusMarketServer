package com.project.api;

import com.project.VO.FriendVO;
import com.project.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "articleFeignClient", url = "http://localhost:8083")
public interface ArticleFeignClient {
    @GetMapping("/friend/attentionApi")
    List<FriendVO> attentionApi(@RequestParam(value = "userId", required = false) Long userId);
}
