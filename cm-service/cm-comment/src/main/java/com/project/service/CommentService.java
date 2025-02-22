package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.CommentVO;
import com.project.domain.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {
    // 根据动态id查询评论
    List<CommentVO> queryCommentByArticleId(Long articleId);

    // 根据评论id删除评论
    boolean deleteByCommentId(Long articleId, Long commentId);

    // 根据动态id删除相关联评论
    boolean deleteByArticleId(Long articleId);

    // 获取自己动态的评论数
    // 根据是否传入id判断是自己的评论数还是别人的评论数
    Map<Long, Integer> queryCommentCount(Long userId);
}
