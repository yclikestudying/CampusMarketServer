package com.project.interceptor;

import com.project.common.ResultCodeEnum;
import com.project.exception.BusinessExceptionHandler;
import com.project.util.TokenUtil;
import com.project.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取Token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            // 如果Token为空，返回401未授权状态码
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(401)));
        }

        // 解析token
        Map<String, Object> map = TokenUtil.parseToken(token);
        Long userId = (Long) map.get("userId");

        // 存入 ThreadLocal
        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContext.clear();
    }
}
