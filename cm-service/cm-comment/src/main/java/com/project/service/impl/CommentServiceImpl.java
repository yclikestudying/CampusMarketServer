package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.ArticleVO;
import com.project.VO.CommentVO;
import com.project.VO.UserVO;
import com.project.api.ArticleFeignClient;
import com.project.api.UserFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Article;
import com.project.domain.Comment;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.CommentMapper;
import com.project.service.CommentService;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private ArticleFeignClient articleFeignClient;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    private final Gson gson = new Gson();

    /**
     * 根据动态id查询评论
     *
     * @param articleId
     * @return
     */
    @Override
    public List<CommentVO> queryCommentByArticleId(Long articleId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(articleId);
        // 查询 Redis
        String fromRedis = redisTemplate.opsForValue().get(RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_COMMENT, articleId));
        List<CommentVO> commentVOList = gson.fromJson(fromRedis, new TypeToken<List<CommentVO>>() {
        }.getType());

        if (commentVOList == null || commentVOList.isEmpty()) {
            // 查询数据库
            List<Comment> comments = commentMapper.selectList(new QueryWrapper<Comment>().eq("article_id", articleId));
            if (comments == null || comments.isEmpty()) {
                return null;
            }
            // 获取用户id
            List<CommentVO> list = new ArrayList<>();
            comments.forEach(comment -> {
                // 调用 cm-user 模块查询用户信息
                UserVO userVO = userFeignClient.getUserInfoByUserIdApi(comment.getUserId());
                CommentVO commentVO = new CommentVO();
                commentVO.setId(comment.getId());
                commentVO.setCommentContent(comment.getCommentContent());
                commentVO.setCreateTime(comment.getCreateTime());
                commentVO.setUserId(userVO.getUserId());
                commentVO.setUserName(userVO.getUserName());
                commentVO.setUserAvatar(userVO.getUserAvatar());
                list.add(commentVO);
            });
            // 存入 Redis
            redisTemplate.opsForValue().set(RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_COMMENT, articleId), gson.toJson(list));
            return list;
        }

        return commentVOList;
    }

    /**
     * 根据评论id删除评论
     *
     * @param commentId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByCommentId(Long articleId, Long commentId) {
        // 验证
        ValidateUtil.validateTwoLongTypeParam(articleId, commentId);

        // 删除数据库记录
        int delete = commentMapper.deleteById(commentId);
        if (delete == 0) {
            log.warn("id为{}的评论不存在", commentId);
            return false;
        }

        // 删除 Redis 记录
        try {
            redisTemplate.setEnableTransactionSupport(true); // 开启事务
            redisTemplate.multi(); // 开始事务
            redisTemplate.delete(RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_COMMENT, articleId)); // 删除操作
            redisTemplate.exec(); // 提交事务
        } catch (Exception e) {
            redisTemplate.discard(); // 丢弃事务
            log.error("Redis 发生异常，操作失败");
            throw new RuntimeException(e);
        }

        return true;
    }

    /**
     * 根据动态id删除相关联评论
     *
     * @param articleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByArticleId(Long articleId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(articleId);

        // 删除数据库记录
        int delete = commentMapper.delete(new QueryWrapper<Comment>().eq("article_id", articleId));
        if (delete == 0) {
            log.warn("id为{}的评论不存在", articleId);
            return false;
        }

        // 删除 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_COMMENT, articleId);
        try {
            // 开启 Redis 事务
            redisTemplate.setEnableTransactionSupport(true); // 启用事务支持
            redisTemplate.multi(); // 开始事务
            redisTemplate.delete(redisKey); // 删除操作
            redisTemplate.exec(); // 提交事务
        } catch (Exception e) {
            // Redis 操作失败，回滚 Redis 事务
            redisTemplate.discard(); // 丢弃事务
            log.error("Redis 发生异常，操作失败");
            throw new RuntimeException(e);
        }

        return true;
    }

    /**
     * 获取自己动态的评论数
     * 根据是否传入id判断是自己的评论数还是别人的评论数
     *
     * @param id
     * @return
     */
    @Override
    public Map<Long, Integer> queryCommentCount(Long id) {
        // 验证是查询自己的还是其他用户的评论数
        Long userId = id == null ? UserContext.getUserId() : id;
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.COMMENT_USER, userId);
        String str = redisTemplate.opsForValue().get(redisKey);
        Map<Long, Integer> commentMap = gson.fromJson(str, new TypeToken<Map<Long, Integer>>() {
        }.getType());
        if (commentMap == null || commentMap.isEmpty()) {
            // Redis 为空，查询数据库
            // 调用 cm-article 模块，查询自己的动态
            List<ArticleVO> list = articleFeignClient.queryArticleByUserIdApi(userId);
            List<Long> collect = null;
            if (list != null && !list.isEmpty()) {
                // 获取动态id
                collect = list.stream().map(ArticleVO::getArticleId).collect(Collectors.toList());
            }
            // 根据每个id查询评论数
            Map<Long, Integer> map = new HashMap<>();
            if (collect != null && !collect.isEmpty()) {
                collect.forEach(articleId -> {
                    Integer count = commentMapper.selectCount(new QueryWrapper<Comment>().eq("article_id", articleId));
                    map.put(articleId, count);
                });
            }
            // 存入 Redis
            redisTemplate.opsForValue().set(redisKey, gson.toJson(map), 24, TimeUnit.HOURS);
            return map;
        }


        return commentMap;
    }
}
