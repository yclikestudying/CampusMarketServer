package com.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品
 */
@TableName(value ="goods")
@Data
public class Goods implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发表用户id
     */
    private Long userId;

    /**
     * 商品内容
     */
    private String goodsContent;

    /**
     * 商品图片
     */
    private String goodsPhotos;

    /**
     * 商品价格
     */
    private Integer goodsPrice;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0-未删除 1-已删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}