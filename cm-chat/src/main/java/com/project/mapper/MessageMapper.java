package com.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.VO.message.MessageListVO;
import com.project.domain.Message;
import com.project.domain.User;
import org.apache.ibatis.annotations.Param;

public interface MessageMapper extends BaseMapper<Message> {
    // 查询最后一条消息相关内容以及发送方相关内容
    User getUserInfo(@Param("id") Long id);
}
