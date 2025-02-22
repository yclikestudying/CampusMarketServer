package com.project.controller;

import com.project.VO.article.ArticleVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.ArticleService;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
     * 查询用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    @GetMapping("/queryArticleByUserId")
    @ApiOperation(value = "查询用户所有动态信息")
    public Result<List<ArticleVO>> queryArticleByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        List<ArticleVO> list = articleService.queryArticleByUserId(userId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 查询关注用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    @GetMapping("/queryArticleOfAttention")
    @ApiOperation(value = "查询关注用户所有动态信息")
    public Result<List<ArticleVO>> queryArticleOfAttention() {
        List<ArticleVO> list = articleService.queryArticleOfAttention(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 查询校园所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    @GetMapping("/queryArticleOfSchool")
    @ApiOperation(value = "查询校园所有动态信息")
    public Result<List<ArticleVO>> queryArticleOfSchool() {
        List<ArticleVO> list = articleService.queryArticleOfSchool(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
