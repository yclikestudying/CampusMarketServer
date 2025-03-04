package com.project.VO.Goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.json.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 我的商品
 */
@Data
public class GoodsVO {
    /**
     * 商品id
     */
    private Long id;

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
