package com.project.controller;

import com.project.VO.ArticleVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.ArticleService;
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

}
