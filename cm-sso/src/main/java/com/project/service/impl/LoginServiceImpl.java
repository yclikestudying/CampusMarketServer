package com.project.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.project.DTO.PhoneDTO;
import com.project.VO.user.UserVO;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.LoginMapper;
import com.project.service.LoginService;
import com.project.util.LoginUtil;
import com.project.util.MD5Util;
import com.project.util.RedisUtil;
import com.project.util.TokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper, User>
        implements LoginService {
    @Resource
    private LoginMapper loginMapper;
    @Resource
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

    /**
     * 手机登录
     * 请求数据:
     * - phone 手机号
     * - password 密码
     * 响应数据:
     * - token 身份标识
     * - user 用户完整信息
     */
    @Override
    public Map<String, Object> phoneLogin(PhoneDTO phoneLoginDTO) {
        // 获取参数
        String phone = phoneLoginDTO.getPhone();
        String password = phoneLoginDTO.getPassword();

        // 验证参数是否符合要求
        LoginUtil.validatePhoneAndPassword(phone, password);

        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_phone", phone);
        User user = loginMapper.selectOne(queryWrapper);

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessExceptionHandler(201, "用户未注册");
        }

        // 验证密码是否正确
        LoginUtil.isPasswordValid(password, user.getUserPassword());

        // 生成 token
        String token = TokenUtil.createToken(user.getUserId(), user.getUserPhone());

        // token 存入 Redis
        String tokenKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_TOKEN, user.getUserId());
        redisUtil.setRedisData(tokenKey, token, 24);

        // 用户信息存入 Redis
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        String infoKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_INFO, user.getUserId());
        redisUtil.setRedisData(infoKey, gson.toJson(userVO), 24);

        // 响应给前端
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", userVO);
        return map;
    }

    /**
     * 手机注册
     * 请求数据:
     * - phone 手机号
     * - password 密码
     * - checkPassword 校验密码
     */
    @Override
    public boolean phoneRegister(PhoneDTO phoneRegisterDTO) {
        // 获取参数
        String phone = phoneRegisterDTO.getPhone();
        String password = phoneRegisterDTO.getPassword();
        String confirmPassword = phoneRegisterDTO.getCheckPassword();

        // 验证参数是否符合要求
        LoginUtil.validatePhoneAndPassword(phone, password);

        // 验证二次密码
        if (!Objects.equals(password, confirmPassword)) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_phone", phone);
        User user = loginMapper.selectOne(queryWrapper);

        if (user != null) {
            throw new BusinessExceptionHandler(201, "用户已注册");
        }

        User one = new User();
        one.setUserPhone(phone);
        one.setUserPassword(MD5Util.calculateMD5(password));
        one.setUserAvatar("/static/my/默认头像.jpg"); // 默认头像
        one.setUserName("默认用户名");
        return loginMapper.insert(one) > 0;
    }
}




