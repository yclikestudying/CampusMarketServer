package com.project.VO;

import lombok.Data;

import java.util.Date;

@Data
public class CommentVO {
    /**
     * id
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
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论时间
     */
    private Date createTime;
}
