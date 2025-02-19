package com.project.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.DTO.PhoneDTO;
import com.project.common.ResultCodeEnum;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.LoginMapper;
import com.project.service.LoginService;
import com.project.util.LoginUtil;
import com.project.util.MD5Util;
import com.project.util.TokenUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper, User>
        implements LoginService {
    @Resource
    private LoginMapper loginMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 手机登录
     *
     * @param phoneLoginDTO 登录信息
     * @return
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

        // 生成token
        String token = TokenUtil.createToken(user.getUserId(), user.getUserPhone());

        // 存入redis
        redisTemplate.opsForValue().set("token", token);

        return Collections.singletonMap("token", token);
    }

    /**
     * 手机注册
     *
     * @param phoneRegisterDTO 手机号码、密码、二次密码
     * @return string
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
        return loginMapper.insert(one) > 0;
    }
}




