package com.project.VO;

import lombok.Data;

@Data
public class LikeVO {
    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 点赞数
     */
    private Integer likeCount;
}
