package com.project.util;

import com.project.common.ResultCodeEnum;
import com.project.exception.BusinessExceptionHandler;

import java.util.Objects;

public class ValidateUtil {
    /**
     * 验证一个 Long 类型参数
     *
     * @param param
     */
    public static void validateSingleLongTypeParam(Long param) {
        if (param == null || param <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
    }

    public static void validateTwoLongTypeParam(Long param1, Long param2) {
        if (param1 == null || param2 == null || param1 <= 0 || param2 <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
    }
}
