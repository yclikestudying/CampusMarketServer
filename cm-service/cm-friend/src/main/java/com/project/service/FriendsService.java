package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.FriendVO;
import com.project.domain.Friends;

import java.util.List;

public interface FriendsService extends IService<Friends> {
    /**
     * 查询我的关注
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    List<FriendVO> attention(Long id);

    /**
     * 查询我的粉丝
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    List<FriendVO> fans(Long id);

    /**
     * 查询互关用户
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    List<FriendVO> attentionAndFans(Long id);

    /**
     * 查询我的关注数量
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - count 关注数量
     */
    Integer attentionCount(Long userId);

    /**
     * 查询我的粉丝数量
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - count 粉丝数量
     */
    Integer fansCount(Long userId);

    /**
     * 查询互关用户数量
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - count 互关用户数量
     */
    Integer attentionAndFansCount(Long userId);

    /**
     * 关注用户
     * 请求数据
     * - userId 我的id
     * - otherId 关注用户的id
     */
    boolean attentionUser(Long userId, Long otherId);

    /**
     * 取消关注用户
     * 请求数据
     * - userId 我的id
     * - otherId 关注用户的id
     */
    boolean unAttentionUser(Long userId, Long otherId);
}
