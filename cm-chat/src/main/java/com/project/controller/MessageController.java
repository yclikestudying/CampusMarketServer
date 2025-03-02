package com.project.controller;

import com.project.VO.MessageVO;
import com.project.VO.message.MessageListVO;
import com.project.common.Result;
import com.project.common.ResultCodeEnum;
import com.project.service.MessageService;
import com.project.util.UserContext;
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

    /**
     * 把未读信息标为已读
     * 请求数据
     * - otherId 聊天对方的id
     */
    @PutMapping("/read")
    @ApiOperation(value = "把未读信息标为已读")
    public Result<String> read(@RequestParam("otherId") Long otherId) {
        boolean result = messageService.read(otherId);
        return result ? Result.success(ResultCodeEnum.SUCCESS) : Result.fail(ResultCodeEnum.FAIL);
    }

    /**
     * 拉取消息列表
     */
    @GetMapping("/queryMessageList")
    @ApiOperation(value = "拉取消息列表")
    public Result<List<MessageListVO>> queryMessageList() {
        List<MessageListVO> list = messageService.queryMessageList(UserContext.getUserId());
        return Result.success(ResultCodeEnum.SUCCESS, list);
    }

    /**
     * 查询所有未读消息
     */
    @GetMapping("/queryUnReadMessage")
    @ApiOperation(value = "查询所有未读消息")
    public Result<Integer> queryUnReadMessage() {
        Integer count = messageService.queryUnReadMessage();
        return Result.success(ResultCodeEnum.SUCCESS, count);
    }
}
