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

    // 查询我的粉丝
    List<FriendVO> fans(Long id);

    // 查询互关用户
    List<FriendVO> attentionAndFans(Long id);

    // 查询我的关注数量
    Integer attentionCount(Long userId);

    // 查询我的粉丝数量
    Integer fansCount(Long userId);

    // 查询互关用户数量
    Integer attentionAndFansCount(Long userId);

    /**
     * 查询校园用户
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    List<FriendVO> school(Long userId);
}
