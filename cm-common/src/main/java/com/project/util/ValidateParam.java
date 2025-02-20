package com.project.util;

import com.project.common.ResultCodeEnum;
import com.project.exception.BusinessExceptionHandler;

import java.util.Objects;

public class ValidateParam {
    public static void validateSingleLongTypeParam(Long param) {
        if (param == null || param <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
    }
}
