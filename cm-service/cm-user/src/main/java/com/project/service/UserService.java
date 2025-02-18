package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.UserVO;
import com.project.domain.User;

public interface UserService extends IService<User> {
    // 根据id查询用户信息
    UserVO getUserInfoByUserId(Long id);
}
