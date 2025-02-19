package com.project.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
@Data
public class ArticleVO {
    /**
     * 动态id
     */
    @TableId(type = IdType.AUTO)
    private Long articleId;

    /**
     * 动态内容
     */
    private String articleContent;

    /**
     * 动态图片
     */
    private String articlePhotos;

    /**
     * 创建时间
     */
    private Date createTime;
}
