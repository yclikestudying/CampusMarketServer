package com.project.DTO;

import lombok.Data;

@Data
public class LoginDTO {
    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 密码
     */
    private String userPassword;
}
