package com.project.util;

import com.project.exception.BusinessExceptionHandler;

public class AdminUtil {
    public static void adminValidate(Integer isAdmin) {
        if (isAdmin == 0) {
            throw new BusinessExceptionHandler(401, "您不是管理员");
        }
    }
}
