package com.project.api;

import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentAPI {
    @Resource
    private CommentService commentService;

    /**
     * 根据动态id删除相关联评论
     *
     * @param articleId
     * @return
     */
    @DeleteMapping("/deleteByArticleId")
    public boolean deleteByArticleId(@RequestParam("articleId") Long articleId) {
        return commentService.deleteByArticleId(articleId);
    }
}
