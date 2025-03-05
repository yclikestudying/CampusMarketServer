package com.project.service.impl;

import com.google.gson.Gson;
import com.project.DTO.LoginDTO;
import com.project.VO.user.UserVO;
import com.project.constants.RedisKeyConstants;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.AdminMapper;
import com.project.service.AdminService;
import com.project.util.MD5Util;
import com.project.util.RedisUtil;
import com.project.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    private AdminMapper adminMapper;
    @Resource
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

    /**
     * 管理员登录
     * 请求数据
     * - userPhone 手机号
     * - userPassword 密码
     */
    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 获取登录参数
        String userPhone = loginDTO.getUserPhone();
        String userPassword = loginDTO.getUserPassword();

        // 验证登录参数
        if (StringUtils.isAnyBlank(userPhone, userPassword)) {
            throw new BusinessExceptionHandler(400, "登录参数错误");
        }

        // 查询数据库，查看该登录管理员是否存在
        User userExist = adminMapper.isUserExist(userPhone);
        if (userExist == null) {
            throw new BusinessExceptionHandler(401, "用户未注册");
        }

        // 管理员验证
        if (userExist.getIsAdmin() == 0) {
            throw new BusinessExceptionHandler(401, "您不是管理员");
        }

        // 密码验证
        String md5 = MD5Util.calculateMD5(userPassword);
        if (!Objects.equals(md5, userExist.getUserPassword())) {
            throw new BusinessExceptionHandler(401, "密码错误");
        }

        Map<String, Object> map = new HashMap<>();
        // 保存用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userExist, userVO);
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ADMIN_INFO, userExist.getUserId());
        redisUtil.setRedisData(redisKey, gson.toJson(userVO));
        map.put("user", userVO);

        // 保存token
        String token = TokenUtil.createToken(userExist.getUserId(), userExist.getUserPhone());
        String redisKey1 = RedisKeyConstants.getRedisKey(RedisKeyConstants.ADMIN_TOKEN, userVO.getUserId());
        redisUtil.setRedisData(redisKey1, token);
        map.put("token", token);
        return map;
    }
}
