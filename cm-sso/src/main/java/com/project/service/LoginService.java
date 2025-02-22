package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.DTO.PhoneDTO;
import com.project.domain.User;

import java.util.Map;

public interface LoginService extends IService<User> {
    /**
     * 手机登录
     * 请求数据:
     * - phone 手机号
     * - password 密码
     * 响应数据:
     * - token 身份标识
     * - user 用户完整信息
     */
    Map<String, Object> phoneLogin(PhoneDTO phoneDTO);

    /**
     * 手机注册
     * 请求数据:
     * - phone 手机号
     * - password 密码
     * - checkPassword 校验密码
     */
    boolean phoneRegister(PhoneDTO phoneDTO);
}
