package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.VO.article.ArticleCommentVO;
import com.project.VO.article.ArticleUserVO;
import com.project.api.UserFeignClient;
import com.project.domain.Comment;
import com.project.mapper.CommentMapper;
import com.project.service.CommentService;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserFeignClient userFeignClient;


    /**
     * 根据动态id查询评论信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 评论数
     * - List<ArticleUserVO> 评论用户
     */
    @Override
    public ArticleCommentVO queryLikeInfo(Long articleId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(articleId);

        // 查询数据库记录
        List<Comment> comments = commentMapper.selectList(new QueryWrapper<Comment>()
                .select("id", "user_id", "comment_content")
                .eq("article_id", articleId));

        if (comments != null && !comments.isEmpty()) {
            ArticleCommentVO articleCommentVO = new ArticleCommentVO();
            // 获取评论
            List<String> commentList = comments.stream().map(Comment::getCommentContent).collect(Collectors.toList());
            articleCommentVO.setCommentList(commentList);
            // 获取评论数
            articleCommentVO.setCount(commentList.size());
            // 获取评论用户信息
            List<Long> userIdList = comments.stream().map(Comment::getUserId).collect(Collectors.toList());
            List<ArticleUserVO> articleUserVOList = userFeignClient.getUserInfoBatch(userIdList);
            articleCommentVO.setArticleUserVOList(articleUserVOList);
            return articleCommentVO;
        }
        return null;
    }
}