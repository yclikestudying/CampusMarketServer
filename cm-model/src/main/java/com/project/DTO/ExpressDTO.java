package com.project.DTO;

import lombok.Data;

/**
 *跑腿内容
 */
@Data
public class ExpressDTO {
    /**
     * 文本内容
     */
    private String content;

    /**
     * 价格
     */
    private String price;
}
