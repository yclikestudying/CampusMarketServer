package com.project.controller;

import com.project.VO.ArticleVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.ArticleService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/article")
@Api(tags = "动态模块")
@Slf4j
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * 根据用户id查询动态
     */
    @GetMapping("/queryArticleByUserId")
    @ApiOperation(value = "根据用户id查询动态")
    public Result<List<ArticleVO>> queryArticleByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        List<ArticleVO> list = articleService.queryArticleByUserId(userId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 查询校园动态（不包括关注的用户的动态）
     */
    @GetMapping("/queryArticle")
    @ApiOperation(value = "查询校园动态")
    public Result<List<ArticleVO>> queryArticle() {
        List<ArticleVO> list = articleService.queryArticle(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 查询关注用户的动态
     */
    @GetMapping("/queryArticleByAttention")
    @ApiOperation(value = "查询关注用户的动态")
    public Result<List<ArticleVO>> queryArticleByAttention() {
        List<ArticleVO> list = articleService.queryArticleByAttention(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 根据用户id查询动态API
     */
    @GetMapping("/queryArticleByUserIdApi")
    @ApiOperation(value = "根据用户id查询动态API")
    public List<ArticleVO> queryArticleByUserIdApi(@RequestParam(value = "userId", required = false) Long userId) {
        return articleService.queryArticleByUserId(userId);
    }

    /**
     * 查询校园动态API（不包括关注的用户的动态）
     */
    @GetMapping("/queryArticleApi")
    @ApiOperation(value = "查询校园动态API")
    public List<ArticleVO> queryArticleApi() {
        return articleService.queryArticle(UserContext.getUserId());
    }

    /**
     * 查询关注用户的动态API
     */
    @GetMapping("/queryArticleByAttentionApi")
    @ApiOperation(value = "查询关注用户的动态API")
    public List<ArticleVO> queryArticleByAttentionApi() {
        return articleService.queryArticleByAttention(UserContext.getUserId());
    }
}
