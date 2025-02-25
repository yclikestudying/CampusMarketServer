package com.project.util;

import com.project.common.ResultCodeEnum;
import com.project.exception.BusinessExceptionHandler;

import java.util.Objects;

/**
 * 参数验证工具类
 */
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

    /**
     * 验证两个 Long 类型参数
     *
     * @param param1
     * @param param2
     */
    public static void validateTwoLongTypeParam(Long param1, Long param2) {
        if (param1 == null || param2 == null || param1 <= 0 || param2 <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
    }

    /**
     * 验证三个 Long 类型参数
     *
     * @param param1
     * @param param2
     */
    public static void validateThreeLongTypeParam(Long param1, Long param2, Long param3) {
        if (param1 == null || param2 == null ||  param3 == null || param1 <= 0 || param2 <= 0 || param3 <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
    }

    /**
     * 验证通用接口参数
     * id == null，根据 UserContext 提供的 id 进行操作
     * id != null，根据 id 本身进行操作
     *
     * @param id
     * @return
     */
    public static Long validateUserId(Long id) {
        return id == null ? UserContext.getUserId() : id;
    }
}
