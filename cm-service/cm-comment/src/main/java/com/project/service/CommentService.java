package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.DTO.CommentDTO;
import com.project.VO.CommentVO;
import com.project.VO.article.ArticleCommentVO;
import com.project.domain.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {
    /**
     * 根据动态id查询评论信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 评论数
     * - List<ArticleUserVO> 评论用户
     */
    ArticleCommentVO queryCommentInfo(Long articleId);

    /**
     * 根据动态id删除评论信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - int 删除的行数
     */
    int deleteCommentInfo(Long articleId);

    /**
     * 发表评论
     * 请求数据
     * - articleId 动态id
     * - comment 评论内容
     */
    boolean publish(CommentDTO commentDTO);

    /**
     * 删除评论
     * 请求数据
     * - articleId 动态id
     * - commentId 评论id
     */
    boolean delete(Long articleId, Long commentId, Long userId);
}
