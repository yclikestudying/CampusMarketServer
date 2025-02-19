package com.project.DTO;

import lombok.Data;

@Data
public class PhoneDTO {

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 二次密码
     */
    private String checkPassword;
}
