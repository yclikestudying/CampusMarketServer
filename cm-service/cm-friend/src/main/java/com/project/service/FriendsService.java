package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.FriendVO;
import com.project.domain.Friends;

import java.util.List;

public interface FriendsService extends IService<Friends> {
    // 查询我的关注
    List<FriendVO> attention(Long id);

    // 查询我的粉丝
    List<FriendVO> fans(Long id);

    // 查询互关用户
    List<FriendVO> attentionAndFans(Long id);
}
