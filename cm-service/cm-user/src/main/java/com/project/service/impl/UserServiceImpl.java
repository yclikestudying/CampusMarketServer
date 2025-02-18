package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.VO.UserVO;
import com.project.common.ResultCodeEnum;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.UserMapper;
import com.project.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 根据id查询用户信息
     *
     * @param id 用户id
     * @return
     */
    @Override
    public UserVO getUserInfoByUserId(Long id) {
        // 验证id
        if (id == null || id <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_id", id));
        if (user == null) {
            throw new BusinessExceptionHandler(201, "用户不存在");
        }

        // 数据脱敏
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
