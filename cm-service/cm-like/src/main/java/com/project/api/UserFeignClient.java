package com.project.api;

import com.project.VO.article.ArticleUserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "userFeignClient", url = "http://localhost:8082")
public interface UserFeignClient {
    /**
     * 批量查询用户信息
     * 请求数据:
     * - List<Long> 用户id集合
     * 响应数据:
     * - List<ArticleUserVO> 用户信息集合
     */
    @PostMapping("/api/user/getUserInfoBatch")
    List<ArticleUserVO> getUserInfoBatch(@RequestBody List<Long> userIds);
}
