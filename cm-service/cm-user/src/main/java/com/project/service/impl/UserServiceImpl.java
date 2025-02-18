package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.VO.UserVO;
import com.project.common.ResultCodeEnum;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.UserMapper;
import com.project.service.UserService;
import com.project.util.TokenUtil;
import com.project.util.UploadAvatar;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
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

    /**
     * 修改用户信息
     *
     * @param userId
     * @param map
     * @return
     */
    @Override
    public boolean updateUser(Long userId, Map<String, Object> map) {
        // 获取要修改的参数名和参数值
        String key = (String) map.get("key");
        if (StringUtils.isBlank(key)) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
        String value = null;
        Integer gender = null;
        if (!Objects.equals(key, "userGender")) {
            value = (String) map.get("value");
            if (StringUtils.isBlank(value)) {
                throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
            }
        } else {
            gender = (Integer) map.get("value");
            if (gender == null) {
                throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
            }
        }

        // 选择要修改的数据
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        User user = new User();
        switch (key) {
            // 修改用户名
            case "userName":
                updateWrapper.set("user_name", value);
                user.setUserName(value);
                break;
            // 修改密码
            case "userPassword":
                // todo 旧密码匹对 新密码加密修改
                updateWrapper.set("user_password", value);
                user.setUserPassword(value);
                break;
            // 修改性别
            case "userGender":
                updateWrapper.set("user_gender", gender);
                user.setUserGender(gender);
                break;
            // 修改生日
            case "userBirthday":
                updateWrapper.set("user_birthday", value);
                user.setUserBirthday(value);
                break;
            // 修改简介
            case "userProfile":
                updateWrapper.set("user_profile", value);
                user.setUserProfile(value);
                break;
            // 修改所在地
            case "userLocation":
                updateWrapper.set("user_location", value);
                user.setUserLocation(value);
                break;
            // 修改家乡
            case "userHomeTown":
                updateWrapper.set("user_hometown", value);
                user.setUserHometown(value);
                break;
            // 修改专业
            case "userProfession":
                updateWrapper.set("user_profession", value);
                user.setUserProfession(value);
                break;
            // 修改标签
            case "userTags":
                updateWrapper.set("user_tags", value);
                user.setUserTags(value);
                break;
            default:
                throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 修改数据库数据
        return userMapper.update(user, updateWrapper) > 0;
    }

    /**
     * 修改用户头像
     *
     * @param userId 用户id
     * @param file 文件
     * @return
     */
    @Override
    public boolean updateAvatar(Long userId, MultipartFile file) {
        // 验证参数
        if (file == null || file.isEmpty()) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 上传照片到阿里云服务器，并返回新的访问地址
        String newLink = null;
        try {
            newLink = UploadAvatar.uploadAvatar(file, "avatar");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 存储照片地址
        User user = new User();
        user.setUserId(userId);
        user.setUserAvatar(newLink);
        userMapper.updateById(user);
        return true;
    }
}
