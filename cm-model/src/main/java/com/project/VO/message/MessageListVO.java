package com.project.VO.message;

import lombok.Data;

import java.util.Date;

/**
 * 消息列表
 */
@Data
public class MessageListVO {
    /**
     * 主键
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户名字
     */
    private String userName;

    /**
     * 最后一条消息内容
     */
    private String content;

    /**
     * 该条消息发送者
     * 可能是我，可能是其他用户
     */
    private Long sendUserId;

    /**
     * 内容类型
     * 文本或者图片
     */
    private String type;

    /**
     * 最后一条消息的发送时间
     */
    private Date createTime;

    /**
     * 未读消息数量
     */
    private Integer unReadMessageCount;
}
