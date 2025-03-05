package com.project.mapper;

import com.project.domain.User;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper {
    // 查询用户是否存在
    User isUserExist(@Param("userPhone") String userPhone);
}
