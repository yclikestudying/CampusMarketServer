package com.project.VO.Goods;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.json.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class GoodsListVO {
    /**
     * 商品id
     */
    private Long id;

    /**
     * 发布者id
     */
    private Long userId;

    /**
     * 发布者头像
     */
    private String userAvatar;

    /**
     * 发布者名称
     */
    private String userName;

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
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
}
