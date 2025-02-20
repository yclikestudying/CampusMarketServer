package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.CommentVO;
import com.project.domain.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    // 根据动态id查询评论
    List<CommentVO> queryCommentByArticleId(Long articleId);

    // 根据评论id删除评论
    boolean deleteByCommentId(Long articleId, Long commentId);

    // 根据动态id删除相关联评论
    boolean deleteByArticleId(Long articleId);
}
