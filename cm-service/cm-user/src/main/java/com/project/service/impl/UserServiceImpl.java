package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.project.VO.FriendVO;
import com.project.VO.UserVO;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.UserMapper;
import com.project.service.UserService;
import com.project.util.TokenUtil;
import com.project.util.UploadAvatar;
import com.project.util.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private final Gson gson = new Gson();

    /**
     * 根据id查询用户信息
     *
     * @param id 用户id
     * @return
     */
    @Override
    public UserVO getUserInfoByUserId(Long id) {
        // 验证id
        Long userId = getUserId(id);
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // redis查询
        String userStr = redisTemplate.opsForValue().get(RedisKeyConstants.getUserInfoKey(userId));
        UserVO userVO = gson.fromJson(userStr, UserVO.class);
        if (userVO == null) {
            // redis为空，查询数据库
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_id", userId));
            if (user == null) {
                throw new BusinessExceptionHandler(200, "用户不存在");
            }
            userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            // 存入redis
            redisTemplate.opsForValue().set(RedisKeyConstants.getUserInfoKey(userId), gson.toJson(userVO));
        }

        return userVO;
    }

    /**
     * 修改用户信息
     *
     * @param userId
     * @param map
     * @return
     */
    @Transactional
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
        int result = userMapper.update(user, updateWrapper);

        // 删除redis缓存
        Boolean delete = redisTemplate.delete(RedisKeyConstants.getUserInfoKey(userId));

        if (result <= 0 || delete == null || !delete) {
            throw new BusinessExceptionHandler(200, "数据库操作失败");
        }

        return true;
    }

    /**
     * 修改用户头像
     *
     * @param userId 用户id
     * @param file   文件
     * @return
     */
    @Transactional
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
        int result = userMapper.updateById(user);

        // 删除redis缓存
        Boolean delete = redisTemplate.delete(RedisKeyConstants.getUserInfoKey(userId));

        if (result <= 0 || delete == null || !delete) {
            throw new BusinessExceptionHandler(200, "数据库操作失败");
        }

        return true;
    }

    /**
     * 批量查询用户
     *
     * @param ids
     * @return
     */
    @Override
    public List<FriendVO> getUserInfo(List<Long> ids) {
        // 验证
        if (ids.isEmpty()) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询
        List<User> users = userMapper.selectBatchIds(ids);
        if (users != null && !users.isEmpty()) {
            // 脱敏
            return users.stream().map(user -> {
                FriendVO friendVO = new FriendVO();
                friendVO.setUserId(user.getUserId());
                friendVO.setUserAvatar(user.getUserAvatar());
                friendVO.setUserName(user.getUserName());
                friendVO.setUserProfile(user.getUserProfile());
                return friendVO;
            }).collect(Collectors.toList());
        }

        return null;
    }

    private Long getUserId(Long id) {
        // id存在，查询别人的信息
        // id不存在，查询自己的信息
        return id == null ? UserContext.getUserId() : id;
    }
}
