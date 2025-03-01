package com.project.controller;

import com.project.VO.FriendVO;
import com.project.VO.VisitVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.VisitService;
import com.project.util.RedisUtil;
import com.project.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/visit")
@Api(tags = "访客模块")
public class VisitController {
    @Resource
    private VisitService visitService;

    /**
     * 记录访客记录
     * 请求数据
     * - visitedId 被访问者id
     */
    @PutMapping("/addVisit")
    @ApiOperation(value = "记录访客记录")
    public Result<String> visit (@RequestParam("visitedId") Long visitedId) {
        boolean result = visitService.visit(visitedId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 查询访客记录
     */
    @GetMapping("/queryVisit")
    @ApiOperation(value = "查询访客记录")
    public Result<List<VisitVO>> queryVisit() {
        List<VisitVO> list = visitService.queryVisit(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
