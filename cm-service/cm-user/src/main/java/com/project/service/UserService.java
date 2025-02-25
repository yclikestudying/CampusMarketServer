package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.FriendVO;
import com.project.VO.article.ArticleUserVO;
import com.project.VO.user.UserInfoVO;
import com.project.VO.user.UserVO;
import com.project.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    /**
     * 查询用户信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - userVO 用户信息
     */
    UserVO getUserInfoByUserId(Long id);

    /**
     * 修改用户个人信息
     * 请求数据:
     * - key 键值
     * - value 值
     */
    boolean updateUser(Long userId, Map<String, Object> map);

    /**
     * 修改用户个人头像
     * 请求数据:
     * - file 图片二进制数据
     */
    boolean updateAvatar(Long userId, MultipartFile file);

    // ===============================外部使用api===============================
    /**
     * 查询用户信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - userId 用户id
     * - userName 用户名称
     * - userAvatar 用户头像
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 批量查询用户信息
     * 请求数据:
     * - List<Long> 用户id集合
     * 响应数据:
     * - List<ArticleUserVO> 用户信息集合
     */
    List<ArticleUserVO> getUserInfoBatch(List<Long> userIds);

    /**
     * 批量查询用户信息（关注相关的用户查询）
     * 请求数据:
     * - List<Long> 用户id集合
     * 响应数据:
     * - List<FriendVO> 用户信息集合
     */
    List<FriendVO> getUserBatch(List<Long> userIds);

    /**
     * 模糊搜索用户
     * 请求数据
     * - keyword 用户名
     * 响应数据
     * - List<FriendVO> 用户集合
     */
    List<FriendVO> queryLikeUser(String keyword);
}
