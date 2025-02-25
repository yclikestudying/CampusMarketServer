package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.project.VO.FriendVO;
import com.project.VO.article.ArticleUserVO;
import com.project.VO.user.UserInfoVO;
import com.project.VO.user.UserVO;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Friends;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.UserMapper;
import com.project.service.UserService;
import com.project.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisUtil redisUtil;

    private final Gson gson = new Gson();

    /**
     * 查询用户信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - userVO 用户信息
     */
    @Override
    public UserVO getUserInfoByUserId(Long id) {
        // 验证参数
        Long userId = ValidateUtil.validateUserId(id);
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis 记录
        String infoKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_INFO, userId);
        String redisData = redisUtil.getRedisData(infoKey);
        UserVO userVO = gson.fromJson(redisData, UserVO.class);
        if (userVO == null) {
            // Redis 为空，查询数据库
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_id", userId));
            if (user == null) {
                throw new BusinessExceptionHandler(200, "用户不存在");
            }
            userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            // 存入 Redis
            redisUtil.setRedisData(infoKey, gson.toJson(userVO));
        }

        return userVO;
    }

    /**
     * 修改用户个人信息
     * 请求数据:
     * - key 键值
     * - value 值
     */
    @Transactional(rollbackFor = Exception.class)
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
        if (result == 0) {
            log.error("用户信息修改失败");
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(200)));
        }

        // 开启 Redis 事务进行操作
        String infoKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_INFO, userId);
        redisUtil.redisTransaction(infoKey);

        return true;
    }

    /**
     * 修改用户个人头像
     * 请求数据:
     * - file 图片二进制数据
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateAvatar(Long userId, MultipartFile file) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(userId);
        if (file == null || file.isEmpty()) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 上传照片到阿里云服务器，并返回新的访问地址
        String newLink;
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
        if (result == 0) {
            log.error("头像修改失败");
            return false;
        }

        // 开启 Redis 事务进行操作
        String infoKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_INFO, userId);
        redisUtil.redisTransaction(infoKey);

        return true;
    }

    /**
     * 查询用户信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - userId 用户id
     * - userName 用户名称
     * - userAvatar 用户头像
     */
    @Override
    public UserInfoVO getUserInfo(Long userId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询数据库记录
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select("user_id", "user_avatar", "user_name")
                .eq("user_id", userId));

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    /**
     * 批量查询用户信息
     * 请求数据:
     * - List<Long> 用户id集合
     * 响应数据:
     * - List<ArticleUserVO> 用户信息集合
     */
    @Override
    public List<ArticleUserVO> getUserInfoBatch(List<Long> userIds) {
        List<ArticleUserVO> articleUserVOList = new ArrayList<>();
        if (userIds != null && !userIds.isEmpty()) {
            userIds.forEach(id -> {
                User user = userMapper.selectOne(new QueryWrapper<User>()
                        .select("user_id", "user_name", "user_avatar")
                        .eq("user_id", id));
                ArticleUserVO articleUserVO = new ArticleUserVO();
                BeanUtils.copyProperties(user, articleUserVO);
                articleUserVOList.add(articleUserVO);
            });
            return articleUserVOList;
        }
        return null;
    }

    /**
     * 批量查询用户信息（关注相关的用户查询）
     * 请求数据:
     * - List<Long> 用户id集合
     * 响应数据:
     * - List<FriendVO> 用户信息集合
     */
    @Override
    public List<FriendVO> getUserBatch(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        List<FriendVO> friendVOList = new ArrayList<>();
        // 查询数据库记录
        List<User> users = userMapper.selectBatchIds(userIds);
        users.forEach(user -> {
            FriendVO friendVO = new FriendVO();
            BeanUtils.copyProperties(user, friendVO);
            friendVOList.add(friendVO);
        });
        return friendVOList;
    }

    /**
     * 模糊搜索用户
     * 请求数据
     * - keyword 用户名
     * 响应数据
     * - List<FriendVO> 用户集合
     */
    @Override
    public List<FriendVO> queryLikeUser(String keyword) {
        // 查询部分用户
        List<User> users = userMapper.selectList(new QueryWrapper<User>()
                .select("user_id", "user_name", "user_avatar", "user_profile")
                .like("user_name", keyword));

        // 过滤掉自己
        List<User> userList = users.stream().filter(user -> !Objects.equals(user.getUserId(), UserContext.getUserId())).collect(Collectors.toList());

        List<FriendVO> list = new ArrayList<>();
        if (!userList.isEmpty()) {
            users.forEach(user -> {
                FriendVO friendVO = new FriendVO();
                BeanUtils.copyProperties(user, friendVO);
                list.add(friendVO);
            });
        }

        return list;
    }
}
