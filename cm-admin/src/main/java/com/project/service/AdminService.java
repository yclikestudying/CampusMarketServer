package com.project.service;

import com.project.DTO.LoginDTO;

import java.util.Map;

public interface AdminService {
    /**
     * 管理员登录
     * 请求数据
     * - userPhone 手机号
     * - userPassword 密码
     */
    Map<String, Object> login(LoginDTO loginDTO);
}
