package com.project.VO;

import lombok.Data;

@Data
public class FriendVO {
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

    /**
     * 用户简介
     */
    private String userProfile;
}
