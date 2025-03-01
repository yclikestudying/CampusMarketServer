package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.DTO.MessageDTO;
import com.project.VO.MessageVO;
import com.project.domain.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService extends IService<Message> {
    /**
     * 上传图片并返回访问地址
     * 请求数据
     * - file 图片
     * 响应数据
     * - string 图片访问地址
     */
    String uploadImage(MultipartFile file);

    /**
     * 保存发送的消息
     */
    boolean saveMessage(MessageDTO messageDTO);

    /**
     * 查询聊天记录
     * 请求数据
     * - otherId 聊天对方的id
     */
    List<MessageVO> queryMessage(Long otherId);

    /**
     * 把未读信息标为已读
     * 请求数据
     * - otherId 聊天对方的id
     */
    boolean read(Long otherId);
}
