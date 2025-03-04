package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.DTO.MessageDTO;
import com.project.VO.MessageVO;
import com.project.VO.message.MessageListVO;
import com.project.VO.user.UserInfoVO;
import com.project.api.UserFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Message;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.MessageMapper;
import com.project.service.MessageService;
import com.project.util.RedisUtil;
import com.project.util.UploadAvatar;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private ThreadPoolTaskExecutor executor;
    @Resource
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        BeanUtils.copyProperties(messageDTO, message);
        int insert = messageMapper.insert(message);
        if (insert == 0) {
            log.warn("消息保存失败");
            return false;
        }

        // 清空我的消息列表
        redisUtil.redisTransaction(RedisKeyConstants
                .getRedisKey(RedisKeyConstants.MESSAGE_LIST, messageDTO.getSendUserId()));
        redisUtil.redisTransaction(RedisKeyConstants
                .getRedisKey(RedisKeyConstants.MESSAGE_LIST, messageDTO.getAcceptUserId()));
        // 清空与某个用户的聊天记录
        redisUtil.redisTransaction(RedisKeyConstants
                .getRedisKey(RedisKeyConstants.MESSAGE_ARRAY, messageDTO.getSendUserId()));
        redisUtil.redisTransaction(RedisKeyConstants
                .getRedisKey(RedisKeyConstants.MESSAGE_ARRAY, messageDTO.getAcceptUserId()));
        return true;
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

        // 查询 Redis
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.MESSAGE_ARRAY, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<MessageVO> messageVOList = gson.fromJson(redisData, new TypeToken<List<MessageVO>>() {
        }.getType());

        // Redis 为空
        // 查询数据库记录
        if (messageVOList == null || messageVOList.isEmpty()) {
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
            if (!list.isEmpty()) {
                // 存入 Redis
                redisUtil.setRedisData(redisKey, gson.toJson(list));
                return list;
            }
        }
        return messageVOList;
    }

    /**
     * 把未读信息标为已读
     * 请求数据
     * - otherId 聊天对方的id
     */
    @Override
    public boolean read(Long otherId) {
        ValidateUtil.validateSingleLongTypeParam(otherId);
        Long userId = UserContext.getUserId();

        // 把对方发给我的消息全部标为已读
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("send_user_id", otherId)
                .eq("accept_user_id", userId)
                .set("is_read", 1);
        this.update(updateWrapper);

        // 清空我的消息列表
        redisUtil.redisTransaction(RedisKeyConstants
                .getRedisKey(RedisKeyConstants.MESSAGE_LIST, userId));

        return true;
    }

    /**
     * 拉取消息列表
     */
    @Override
    public List<MessageListVO> queryMessageList(Long userId) {
        // 查询 Redis 记录
        // 用户消息列表
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.MESSAGE_LIST, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<MessageListVO> list = gson.fromJson(redisData, new TypeToken<List<MessageListVO>>() {
        }.getType());

        // Redis 为空
        // 查询数据库
        if (list == null || list.isEmpty()) {
            // 查询出所有消息中对方用户的id
            List<Message> messages = messageMapper.selectList(new QueryWrapper<Message>()
                    .select("send_user_id")
                    .eq("accept_user_id", userId));
            Set<Long> sendUserIdList = messages.stream().map(Message::getSendUserId).collect(Collectors.toSet());
            List<Message> messages1 = messageMapper.selectList(new QueryWrapper<Message>()
                    .select("accept_user_id")
                    .eq("send_user_id", userId));
            Set<Long> acceptUserIdList = messages1.stream().map(Message::getAcceptUserId).collect(Collectors.toSet());
            Set<Long> idList = new HashSet<>();
            if (!sendUserIdList.isEmpty()) {
                idList.addAll(sendUserIdList);
            }
            if (!acceptUserIdList.isEmpty()) {
                idList.addAll(acceptUserIdList);
            }

            // 判断查询是否为空，不为空，则获取id集合
            if (!idList.isEmpty()) {
                // 使用线程池并发处理每个用户的查询
                List<CompletableFuture<MessageListVO>> futures = idList.stream()
                        .map(id -> CompletableFuture.supplyAsync(() -> {
                            // 根据每个id查询出聊天对象的部分用户信息
                            // 用户id、用户头像、用户名称
                            MessageListVO messageListVO = new MessageListVO();
                            User user = messageMapper.getUserInfo(id);

                            // 查询出最后一条消息的内容以及发送者
                            // 消息内容、发送者id、消息类型、发送时间
                            Message message = messageMapper.selectOne(new QueryWrapper<Message>()
                                    .select("content", "send_user_id", "type", "create_time")
                                    .and(wrapper -> wrapper
                                            .eq("send_user_id", id).eq("accept_user_id", userId)
                                            .or()
                                            .eq("send_user_id", userId).eq("accept_user_id", id)
                                    ).orderByDesc("create_time") // 按时间字段降序排序
                                    .last("LIMIT 1"));


                            // 统计每一个用户给我发送的消息的未读数量
                            // 未读数量
                            Integer count = messageMapper.selectCount(new QueryWrapper<Message>()
                                    .eq("send_user_id", id)
                                    .eq("accept_user_id", userId)
                                    .eq("is_read", 0));

                            // 将数据设置到VO中
                            messageListVO.setUserId(user.getUserId());
                            messageListVO.setUserName(user.getUserName());
                            messageListVO.setUserAvatar(user.getUserAvatar());
                            messageListVO.setContent(message.getContent());
                            messageListVO.setSendUserId(message.getSendUserId());
                            messageListVO.setType(message.getType());
                            messageListVO.setCreateTime(message.getCreateTime());
                            messageListVO.setUnReadMessageCount(count);
                            return messageListVO;
                        }, executor)) // 使用自定义线程池
                        .collect(Collectors.toList());

                // 等待所有任务完成，并收集结果
                try {
                    List<MessageListVO> collect = futures.stream()
                            .map(CompletableFuture::join) // 阻塞等待每个任务完成
                            .collect(Collectors.toList());

                    if (!collect.isEmpty()) {
                        // 存入 Redis
                        redisUtil.setRedisData(redisKey, gson.toJson(collect));
                        return collect;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return list;
    }

    /**
     * 查询所有未读消息
     */
    @Override
    public Integer queryUnReadMessage() {
        return messageMapper.selectCount(new QueryWrapper<Message>()
                .eq("accept_user_id", UserContext.getUserId())
                .eq("is_read", 0));
    }
}
