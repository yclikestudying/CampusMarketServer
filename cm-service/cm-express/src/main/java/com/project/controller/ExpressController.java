package com.project.controller;

import com.project.DTO.ExpressDTO;
import com.project.VO.express.ExpressListVO;
import com.project.VO.express.ExpressVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.ExpressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/express")
@Api(tags = "跑腿服务")
public class ExpressController {
    @Resource
    private ExpressService expressService;

    /**
     * 跑腿内容上传
     * 请求数据
     * - content 文本内容
     * - price 价格
     */
    @PostMapping("/upload")
    @ApiOperation(value = "跑腿内容上传")
    public Result<String> upload(@RequestBody ExpressDTO expressDTO) {
        boolean result = expressService.upload(expressDTO);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询自己发布的跑腿服务
     * 请求数据
     * - userId 用户id
     */
    @GetMapping("queryMyExpress")
    @ApiOperation(value = "查询自己发布的跑腿服务")
    public Result<List<ExpressVO>> queryMyExpress(@RequestParam(value = "userId", required = false) Long userId) {
        List<ExpressVO> list = expressService.queryMyExpress(userId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 查询所有的跑腿服务（排除自己的）
     */
    @GetMapping("/queryAllExpress")
    @ApiOperation(value = "查询所有的跑腿服务（排除自己的）")
    public Result<List<ExpressListVO>> queryAllExpress() {
        List<ExpressListVO> list = expressService.queryAllExpress();
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 删除自己的跑腿服务
     * 请求数据
     * - expressId 跑腿服务id
     */
    @DeleteMapping("/deleteExpress")
    @ApiOperation(value = "删除自己的跑腿服务")
    public Result<String> deleteExpress(@RequestParam("expressId") Long expressId) {
        boolean result = expressService.deleteExpress(expressId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }
}
