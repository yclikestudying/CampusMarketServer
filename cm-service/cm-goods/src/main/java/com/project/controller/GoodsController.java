package com.project.controller;

import com.project.VO.Goods.GoodsListVO;
import com.project.VO.Goods.GoodsVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
@Api(tags = "商品模块")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    /**
     * 商品上传
     */
    @RequestMapping("/uploadGoods")
    @ApiOperation(value = "商品上传")
    public Result<String> uploadGoods(@RequestParam(value = "file0", required = false) MultipartFile file0,
                                      @RequestParam(value = "file1", required = false) MultipartFile file1,
                                      @RequestParam(value = "file2", required = false) MultipartFile file2,
                                      @RequestParam(value = "file3", required = false) MultipartFile file3,
                                      @RequestParam(value = "file4", required = false) MultipartFile file4,
                                      @RequestParam(value = "file5", required = false) MultipartFile file5,
                                      @RequestParam(value = "file6", required = false) MultipartFile file6,
                                      @RequestParam(value = "file7", required = false) MultipartFile file7,
                                      @RequestParam(value = "file8", required = false) MultipartFile file8,
                                      @RequestParam(value = "text", required = false) String text,
                                      @RequestParam(value = "price", required = false) String price) {

        Map<Integer, Object> map = new HashMap<>();
        if (file0 != null) {
            map.put(0, file0);
        }
        if (file1 != null) {
            map.put(1, file1);
        }
        if (file2 != null) {
            map.put(2, file2);
        }
        if (file3 != null) {
            map.put(3, file3);
        }
        if (file4 != null) {
            map.put(4, file4);
        }
        if (file5 != null) {
            map.put(5, file5);
        }
        if (file6 != null) {
            map.put(6, file6);
        }
        if (file7 != null) {
            map.put(7, file7);
        }
        if (file8 != null) {
            map.put(8, file8);
        }
        boolean result = goodsService.uploadGoods(map, text, price);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询自己发布的商品
     * 请求数据
     * - userId 用户id
     */
    @GetMapping("/queryGoods")
    @ApiOperation(value = "查询自己发布的商品")
    public Result<List<GoodsVO>> queryGoods(@RequestParam(value = "userId", required = false) Long userId) {
        List<GoodsVO> list = goodsService.queryGoods(userId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 根据商品id删除商品
     * 请求数据
     * - goodsId 商品id
     */
    @DeleteMapping("/deleteByGoodsId")
    @ApiOperation(value = "根据商品id删除商品")
    public Result<String> deleteByGoodsId(@RequestParam("goodsId") Long goodsId) {
        boolean result = goodsService.deleteByGoodsId(goodsId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询所有商品（排除自己的）
     */
    @GetMapping("/queryAllGoods")
    @ApiOperation(value = "查询所有商品（排除自己的）")
    public Result<List<GoodsListVO>> queryAllGoods() {
        List<GoodsListVO> list = goodsService.queryAllGoods();
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
