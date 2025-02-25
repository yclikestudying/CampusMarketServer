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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
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

//    /**
//     * 查询用户动态数量
//     * 请求数据:
//     * - userId 用户id
//     * 响应数据:
//     * - count 动态数量
//     */
//    @GetMapping("/queryArticleCount")
//    @ApiOperation(value = "查询校园所有动态信息")
//    public Result<Integer> queryArticleCount() {
//        Integer count = articleService.queryArticleCount(UserContext.getUserId());
//        return Result.success(ResultCodeEnum.SUCCESS, count);
//    }

    /**
     * 根据动态id删除动态以及相关信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - count 动态数量
     */
    @DeleteMapping("/deleteArticleByArticleId")
    @ApiOperation(value = "根据动态id删除动态以及相关信息")
    public Result<String> deleteArticleByArticleId(@RequestParam("articleId") Long articleId) {
        boolean result = articleService.deleteArticleByArticleId(articleId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 上传校园动态
     * 请求数据:
     * - file 图片二进制数据
     * - text 文本内容
     * - count 图片数量
     */
    @PostMapping("/uploadArticle")
    @ApiOperation(value = "上传校园动态")
    public Result<String> uploadArticle(@RequestPart(value = "file0", required = false) MultipartFile file0,
                                        @RequestPart(value = "file1", required = false) MultipartFile file1,
                                        @RequestPart(value = "file2", required = false) MultipartFile file2,
                                        @RequestPart(value = "file3", required = false) MultipartFile file3,
                                        @RequestPart(value = "file4", required = false) MultipartFile file4,
                                        @RequestPart(value = "file5", required = false) MultipartFile file5,
                                        @RequestPart(value = "file6", required = false) MultipartFile file6,
                                        @RequestPart(value = "file7", required = false) MultipartFile file7,
                                        @RequestPart(value = "file8", required = false) MultipartFile file8,
                                        @RequestParam(value = "text", required = false) String text) {

        List<MultipartFile> list = new ArrayList<>();
        if (file0 != null) {
            list.add(file0);
        }
        if (file1 != null) {
            list.add(file1);
        }
        if (file2 != null) {
            list.add(file2);
        }
        if (file3 != null) {
            list.add(file3);
        }
        if (file4 != null) {
            list.add(file4);
        }
        if (file5 != null) {
            list.add(file5);
        }
        if (file6 != null) {
            list.add(file6);
        }
        if (file7 != null) {
            list.add(file7);
        }
        if (file8 != null) {
            list.add(file8);
        }

        boolean result = articleService.uploadArticle(list, text);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }
}
