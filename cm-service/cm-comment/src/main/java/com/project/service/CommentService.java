package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.CommentVO;
import com.project.domain.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    // 根据动态id查询评论
    List<CommentVO> queryCommentByArticleId(Long articleId);
}
