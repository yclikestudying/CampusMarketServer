package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.DTO.CommentDTO;
import com.project.VO.article.ArticleCommentVO;
import com.project.VO.article.ArticleUserVO;
import com.project.api.UserFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Comment;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.CommentMapper;
import com.project.service.CommentService;
import com.project.util.RedisUtil;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private RedisUtil redisUtil;


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
                .select("id", "user_id", "comment_content", "create_time")
                .eq("article_id", articleId)
                .orderByDesc("create_time"));

        ArticleCommentVO articleCommentVO = new ArticleCommentVO();
        if (comments != null && !comments.isEmpty()) {
            // 获取评论内容
            List<String> commentList = comments.stream().map(Comment::getCommentContent).collect(Collectors.toList());
            articleCommentVO.setCommentList(commentList);
            // 获取评论id
            List<Long> commentIdList = comments.stream().map(Comment::getId).collect(Collectors.toList());
            articleCommentVO.setCommentId(commentIdList);
            // 获取评论时间
            List<Date> time = comments.stream().map(Comment::getCreateTime).collect(Collectors.toList());
            articleCommentVO.setTime(time);
            // 获取评论数
            articleCommentVO.setCount(commentList.size());
            // 获取评论用户信息
            List<Long> userIdList = comments.stream().map(Comment::getUserId).collect(Collectors.toList());
            List<ArticleUserVO> articleUserVOList = userFeignClient.getUserInfoBatch(userIdList);
            articleCommentVO.setArticleUserVOList(articleUserVOList);
        }
        return articleCommentVO;
    }

    /**
     * 根据动态id删除评论信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - int 删除的行数
     */
    @Override
    public int deleteCommentInfo(Long articleId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(articleId);

        // 删除数据库中评论信息
        return commentMapper.delete(new QueryWrapper<Comment>().eq("article_id", articleId));
    }

    /**
     * 发表评论
     * 请求数据
     * - articleId 动态id
     * - comment 评论内容
     */
    @Override
    public boolean publish(CommentDTO commentDTO) {
        String content = commentDTO.getContent();
        Long articleId = commentDTO.getArticleId();
        Long userId = commentDTO.getUserId();
        // 验证
        ValidateUtil.validateTwoLongTypeParam(articleId, userId);
        if (StringUtils.isBlank(content)) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 数据库记录
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(UserContext.getUserId());
        comment.setCommentContent(content);
        int insert = commentMapper.insert(comment);
        if (insert == 0) {
            log.warn("评论保存数据库失败");
            return false;
        }

        // 更新缓存
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, userId);
        redisUtil.redisTransaction(redisKey);
        return true;
    }

    /**
     * 删除评论
     * 请求数据
     * - articleId 动态id
     * - commentId 评论id
     */
    @Override
    public boolean delete(Long articleId, Long commentId, Long userId) {
        // 验证
        ValidateUtil.validateThreeLongTypeParam(articleId, commentId, userId);

        // 删除数据库记录
        int delete = commentMapper.delete(new QueryWrapper<Comment>()
                .eq("article_id", articleId)
                .eq("id", commentId));
        if (delete == 0) {
            log.warn("评论不存在");
            return false;
        }

        // 删除 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, userId);
        redisUtil.redisTransaction(redisKey);
        return true;
    }
}