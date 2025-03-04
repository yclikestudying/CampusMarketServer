package com.project.VO.express;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.json.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 用于查询自己的跑腿服务
 */
@Data
public class ExpressVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 代取内容文本
     */
    private String expressContent;

    /**
     * 代取内容价格
     */
    private Integer expressPrice;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
}
