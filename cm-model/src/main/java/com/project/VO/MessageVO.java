package com.project.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.json.CustomDateSerializer;
import lombok.Data;

@Data
public class MessageVO implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
}