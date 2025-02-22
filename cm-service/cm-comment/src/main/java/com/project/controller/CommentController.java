package com.project.controller;

import com.project.VO.CommentVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论模块")
@Slf4j
public class CommentController {
    @Resource
    private CommentService commentService;

}
