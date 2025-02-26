package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.article.ArticleLikeVO;
import com.project.VO.article.ArticleUserVO;
import com.project.VO.article.ArticleVO;
import com.project.api.ArticleFeignClient;
import com.project.api.FriendFeignClient;
import com.project.api.UserFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Likes;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.LikesMapper;
import com.project.service.LikesService;
import com.project.util.RedisUtil;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes>
        implements LikesService {
    @Resource
    private LikesMapper likesMapper;
    @Resource
    private UserFeignClient userFeignClient; // 调用 cm-user 模块
    @Resource
    private FriendFeignClient friendFeignClient;
    @Resource
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

    /**
     * 根据动态id查询点赞信息
     * 请求数据:
     * - articleId 动态id
     * 响应数据:
     * - count 点赞数
     * - List<ArticleUserVO> 点赞用户集合
     */
    @Override
    public ArticleLikeVO queryLikeInfo(Long articleId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(articleId);

        // 查询点赞信息
        List<Likes> likeList = likesMapper.selectList(new QueryWrapper<Likes>().eq("article_id", articleId));
        // 获取用户id
        ArticleLikeVO articleLikeVO = new ArticleLikeVO();
        List<Long> userIdList;
        if (likeList != null && !likeList.isEmpty()) {
            userIdList = likeList.stream().map(Likes::getUserId).collect(Collectors.toList());
            // 批量查询用户信息
            List<ArticleUserVO> articleUserVOList = userFeignClient.getUserInfoBatch(userIdList);
            articleLikeVO.setCount(articleUserVOList.size());
            articleLikeVO.setArticleUserVOList(articleUserVOList);
        }
        return articleLikeVO;
    }

    /**
     * 根据动态id进行点赞
     * 请求数据
     * - articleId 动态id
     * - userId 被点赞用户id
     * 响应数据
     * - success 点赞成功
     * 说明
     * - UserContext.getUserId() 我自己
     * - userId 被点赞用户
     */
    @Override
    public String like(Long articleId, Long userId) {
        // 验证参数
        ValidateUtil.validateTwoLongTypeParam(userId, articleId);

        // 查询点赞记录
        Likes likes = likesMapper.selectOne(new QueryWrapper<Likes>()
                .eq("article_id", articleId)
                .eq("user_id", UserContext.getUserId()));
        if (likes != null) {
            log.warn("已经点赞，点赞失败");
            return "不能重复点赞";
        }

        // 保存点赞信息
        Likes one = new Likes();
        one.setArticleId(articleId);
        one.setUserId(UserContext.getUserId());
        int insert = likesMapper.insert(one);
        if (insert > 0) {
            // 更新被点赞人的动态缓存
            String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, userId);
            redisUtil.redisTransaction(redisKey);
            // 查询被点赞人是否是我的关注
            boolean result = friendFeignClient.isAttention(UserContext.getUserId(), userId);
            if (result) {
                redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_ATTENTION, UserContext.getUserId()));
            }
            return "点赞成功";
        }
        return "点赞失败";
    }

    /**
     * 根据动态id取消点赞
     * 请求数据
     * - articleId 动态id
     * - userId 用户id
     * 响应数据
     * - success 取消点赞成功
     * 说明
     * - UserContext.getUserId() 我自己
     * - userId 被点赞用户
     */
    @Override
    public String unlike(Long articleId, Long userId) {
        // 验证参数
        ValidateUtil.validateTwoLongTypeParam(userId, articleId);

        // 查询点赞记录
        Likes likes = likesMapper.selectOne(new QueryWrapper<Likes>()
                .eq("article_id", articleId)
                .eq("user_id", UserContext.getUserId()));
        if (likes == null) {
            log.warn("还未点赞，取消失败");
            return "取消失败";
        }

        // 取消点赞信息
        int delete = likesMapper.delete(new QueryWrapper<Likes>()
                .eq("article_id", articleId)
                .eq("user_id", UserContext.getUserId()));
        if (delete > 0) {
            // 更新被点赞人的动态缓存
            String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, userId);
            redisUtil.redisTransaction(redisKey);
            // 查询被取消点赞用户是否是我的关注
            boolean result = friendFeignClient.isAttention(UserContext.getUserId(), userId);
            if (result) {
                redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_ATTENTION, UserContext.getUserId()));
            }
            return "取消点赞成功";
        }
        return "取消点赞失败";
    }

    /**
     * 根据动态id删除点赞信息
     * 请求数据:
     * - articleId 动态id
     */
    @Override
    public int deleteLikeInfo(Long articleId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(articleId);

        // 删除数据库中动态点赞信息
        return likesMapper.delete(new QueryWrapper<Likes>().eq("article_id", articleId));
    }
}
