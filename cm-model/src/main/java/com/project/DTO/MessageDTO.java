package com.project.DTO;

import java.io.Serializable;
import lombok.Data;
@Data
public class MessageDTO implements Serializable {
    /**
     * 消息
     */
    private String content;

    /**
     * 发送者id
     */
    private Long sendUserId;

    /**
     * 接收者id
     */
    private Long acceptUserId;

    /**
     * 内容类型
     */
    private String type;
}