package com.project.VO.user;

import lombok.Data;

/**
 * 用户重要信息
 */
@Data
public class UserInfoVO {
    /**
     * 用户id
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
}
