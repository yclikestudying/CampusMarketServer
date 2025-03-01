package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.DTO.MessageDTO;
import com.project.VO.MessageVO;
import com.project.common.ResultCodeEnum;
import com.project.domain.Message;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.MessageMapper;
import com.project.service.MessageService;
import com.project.util.UploadAvatar;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {
    @Resource
    private MessageMapper messageMapper;

    /**
     * 上传图片并返回访问地址
     * 请求数据
     * - file 图片
     * 响应数据
     * - string 图片访问地址
     */
    @Override
    public String uploadImage(MultipartFile file) {
        // 验证参数
        if (file == null || file.isEmpty()) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 上传阿里云
        try {
            return UploadAvatar.uploadAvatar(file, "message");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存发送的消息
     */
    @Override
    public boolean saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        BeanUtils.copyProperties(messageDTO, message);
        return messageMapper.insert(message) > 0;
    }

    /**
     * 查询聊天记录
     * 请求数据
     * - otherId 聊天对方的id
     */
    @Override
    public List<MessageVO> queryMessage(Long otherId) {
        ValidateUtil.validateSingleLongTypeParam(otherId);
        Long userId = UserContext.getUserId();

        // 查询数据库记录
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "content", "send_user_id", "accept_user_id", "type", "create_time")
                .and(wrapper -> wrapper
                        .eq("send_user_id", userId)
                        .eq("accept_user_id", otherId)
                )
                .or(wrapper -> wrapper
                        .eq("send_user_id", otherId)
                        .eq("accept_user_id", userId)
                );
        List<Message> messages = messageMapper.selectList(queryWrapper);
        List<MessageVO> list = new ArrayList<>();
        if (!messages.isEmpty()) {
            messages.forEach(message -> {
                MessageVO messageVO = new MessageVO();
                BeanUtils.copyProperties(message, messageVO);
                list.add(messageVO);
            });
        }

        return list;
    }
}
