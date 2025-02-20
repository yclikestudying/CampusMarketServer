package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.domain.Likes;
import java.util.Map;

public interface LikesService extends IService<Likes> {
    // 获取自己的动态的点赞
    Map<Long, Integer> queryLikesOfUser(Long id);

    // 获取校园动态的点赞
    Map<Long, Integer> queryLikesOfCampus(Long userId);

    // 获取关注用户动态的点赞
    Map<Long, Integer> queryLikesOfAttention(Long userId);

    // 根据动态id进行点赞
    String like(Long userId, Long articleId);
    // 根据动态id取消点赞
    String unlike(Long userId, Long articleId);
}
