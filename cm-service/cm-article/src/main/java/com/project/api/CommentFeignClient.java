package com.project.api;

import com.project.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commentFeignClient", url = "http://localhost:8086")
public interface CommentFeignClient {
    /**
     * 根据动态id删除相关联评论
     *
     * @param articleId
     * @return
     */
    @DeleteMapping("/comment/deleteByArticleId")
    boolean deleteByArticleId(@RequestParam("articleId") Long articleId);
}
