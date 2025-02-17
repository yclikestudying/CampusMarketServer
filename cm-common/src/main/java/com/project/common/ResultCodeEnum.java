package com.project.common;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    SYSTEM_ERROR(500, "系统错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    DATA_NOT_FOUND(1001, "数据未找到"),
    DATA_EXISTS(1002, "数据已存在"),
    PARAM_VALIDATE_FAIL(1003, "参数校验失败"),
    USER_NOT_LOGIN(1004, "用户未登录"),
    PERMISSION_DENIED(1005, "权限不足");

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    // 根据code获取对应的枚举常量
    public static ResultCodeEnum getByCode(Integer code) {
        for (ResultCodeEnum resultCode : ResultCodeEnum.values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return null;
    }
}
