package com.project.VO;

import lombok.Data;

import java.util.Date;
@Data
public class VisitVO {
    /**
     * 访问者id
     */
    private Long userId;

    /**
     * 访问者用户名
     */
    private String userName;

    /**
     * 访问者头像
     */
    private String userAvatar;

    /**
     * 访问者简介
     */
    private String userProfile;

    /**
     * 访问时间
     */
    private Date visitTime;
}
