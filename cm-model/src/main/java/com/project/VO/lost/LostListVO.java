package com.project.VO.lost;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.json.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class LostListVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 丢失物品类型（失物招领、寻物启事）
     */
    private String lostType;

    /**
     * 丢失物品名称
     */
    private String lostName;

    /**
     * 丢失物品描述
     */
    private String lostDescription;

    /**
     * 丢失物品图片
     */
    private String lostPhoto;

    /**
     * 发布时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
}
