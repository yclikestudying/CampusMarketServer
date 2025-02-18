package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.UserVO;
import com.project.domain.User;

import java.util.Map;

public interface UserService extends IService<User> {
    // 根据id查询用户信息
    UserVO getUserInfoByUserId(Long id);

    // 修改用户信息
    boolean updateUser(Long userId, Map<String, Object> map);
}
