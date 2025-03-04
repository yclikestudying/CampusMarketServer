package com.project.VO.express;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.json.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 用于查询所有的跑腿服务（排除自己的）
 */
@Data
public class ExpressListVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 发布用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 代取内容文本
     */
    private String expressContent;

    /**
     * 代取内容价格
     */
    private Integer expressPrice;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
}
