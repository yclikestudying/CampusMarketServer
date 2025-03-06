package com.project.controller;

import com.project.VO.lost.LostListVO;
import com.project.VO.lost.LostVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.LostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/express")
@Api(tags = "失物招领模块")
public class LostController {
    @Resource
    private LostService lostService;

    /**
     * 发布失物招领信息
     * 请求数据
     * - lostType 类型
     * - lostName 名称
     * - lostDescription 描述
     * - file 图片文件
     */
    @PostMapping("/publish")
    @ApiOperation(value = "发布失物招领信息")
    public Result<String> publish(@RequestParam("lostType") String lostType,
                                  @RequestParam("lostName") String lostName,
                                  @RequestParam("lostDescription") String lostDescription,
                                  @RequestParam("files") MultipartFile file) {
        boolean result = lostService.publish(lostType, lostName, lostDescription, file);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询我的失物招领数据
     */
    @GetMapping("/queryMy")
    @ApiOperation(value = "查询我的失物招领数据")
    public Result<List<LostVO>> queryMy(@RequestParam(value = "userId", required = false) Long userId) {
        List<LostVO> list = lostService.queryMy(userId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 删除我的失物招领数据
     */
    @DeleteMapping("/deleteMy")
    @ApiOperation(value = "删除我的失物招领数据")
    public Result<String> deleteMy(@RequestParam("lostId") Long lostId) {
        boolean result = lostService.deleteMy(lostId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询所有失物招领数据
     */
    @GetMapping("/queryAll")
    @ApiOperation(value = "查询所有失物招领数据")
    public Result<List<LostListVO>> queryAll() {
        List<LostListVO> list = lostService.queryAll();
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
