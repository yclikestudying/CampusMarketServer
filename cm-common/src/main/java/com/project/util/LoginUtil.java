package com.project.util;

import com.project.common.ResultCodeEnum;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import org.apache.commons.lang3.StringUtils;


import java.util.Objects;

/**
 * 登录注册工具类
 */
public class LoginUtil {
    /**
     * 验证手机号和密码是否符合要求
     */
    public static void validatePhoneAndPassword(String phone, String password) {
        // 校验参数是否为空
        if (StringUtils.isAnyBlank(phone, password)) {
            throw new BusinessExceptionHandler(400, "手机号或密码为空");
        }

        // 手机号格式验证
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessExceptionHandler(400, "手机号格式不正确");
        }
        // 密码长度验证
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessExceptionHandler(400, "密码长度必须在6到20位之间");
        }
    }

    /**
     * 验证密码是否正确
     */
    public static void isPasswordValid(String inputPassword, String storedPassword) {
        String md5Password = MD5Util.calculateMD5(inputPassword);
        if (!Objects.equals(md5Password, storedPassword)) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
    }
}
