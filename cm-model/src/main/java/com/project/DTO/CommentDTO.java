package com.project.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    /**
     * 动态id
     */
    private Long articleId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 发表用户id
     */
    private Long userId;
}
