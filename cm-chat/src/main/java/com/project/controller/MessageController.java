package com.project.controller;

import com.project.VO.MessageVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/message")
@Api(tags = "通信模块")
public class MessageController {
    @Resource
    private MessageService messageService;

    /**
     * 上传图片并返回访问地址
     * 请求数据
     * - file 图片
     * 响应数据
     * - string 图片访问地址
     */
    @PostMapping("/uploadImage")
    @ApiOperation(value = "上传图片并返回访问地址")
    public Result<String> uploadImage(@RequestPart("file")MultipartFile file) {
        String link = messageService.uploadImage(file);
        return Result.success(ResultCodeEnum.SUCCESS, link);
    }

    /**
     * 查询聊天记录
     * 请求数据
     * - otherId 聊天对方的id
     */
    @GetMapping("/queryMessage")
    @ApiOperation(value = "查询聊天记录")
    public Result<List<MessageVO>> queryMessage(@RequestParam("otherId") Long otherId) {
        List<MessageVO> list = messageService.queryMessage(otherId);
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }
}
