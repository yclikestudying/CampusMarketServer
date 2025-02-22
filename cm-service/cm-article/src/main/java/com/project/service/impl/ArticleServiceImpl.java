package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.FriendVO;
import com.project.VO.article.ArticleCommentVO;
import com.project.VO.article.ArticleLikeVO;
import com.project.VO.user.UserInfoVO;
import com.project.VO.user.UserVO;
import com.project.VO.article.ArticleUserVO;
import com.project.VO.article.ArticleVO;
import com.project.api.CommentFeignClient;
import com.project.api.FriendFeignClient;
import com.project.api.LikeFeignClient;
import com.project.api.UserFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Article;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.ArticleMapper;
import com.project.service.ArticleService;
import com.project.util.RedisUtil;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private UserFeignClient userFeignClient; // cm-user 模块
    @Resource
    private LikeFeignClient likeFeignClient; // cm-like 模块
    @Resource
    private CommentFeignClient commentFeignClient; // cm-comment 模块
    @Resource
    private FriendFeignClient friendFeignClient; // cm-friend 模块
    @Resource
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

    /**
     * 查询用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    @Override
    public List<ArticleVO> queryArticleByUserId(Long id) {
        // 验证
        Long userId = ValidateUtil.validateUserId(id);
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<ArticleVO> articleVOList = gson.fromJson(redisData, new TypeToken<List<ArticleVO>>() {
        }.getType());

        // Redis 为空，查询数据库
        if (articleVOList == null || articleVOList.isEmpty()) {
            // 调用 cm-user 模块，获取动态发布用户相关信息
            UserInfoVO userInfoVO;
            try {
                userInfoVO = userFeignClient.getUserInfo(userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ArticleUserVO publishUser = new ArticleUserVO();
            BeanUtils.copyProperties(userInfoVO, publishUser);

            // 获取动态id、动态内容、动态图片、动态发布时间
            List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .select("article_id", "article_content", "article_photos", "create_time")
                    .eq("user_id", userId));

            // 动态集合
            List<ArticleVO> articleList = new ArrayList<>();
            if (articles != null && !articles.isEmpty()) {
                // 遍历 articles
                articles.forEach(article -> {
                    // 转换动态信息
                    ArticleVO articleVO = new ArticleVO();
                    BeanUtils.copyProperties(article, articleVO);
                    // 根据动态id查询点赞信息
                    ArticleLikeVO articleLikeVO = likeFeignClient.queryLikeInfo(article.getArticleId());
                    // 根据动态id查询动态评论用户
                    ArticleCommentVO articleCommentVO = commentFeignClient.queryCommentInfo(article.getArticleId());
                    // 封装数据
                    articleVO.setPublishUser(publishUser);
                    articleVO.setLike(articleLikeVO);
                    articleVO.setComment(articleCommentVO);
                    articleList.add(articleVO);
                });
            }
            // 缓存数据
            String articleKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, userId);
            redisUtil.setRedisData(articleKey, gson.toJson(articleList));
            return articleList;
        }

        return articleVOList;
    }

    /**
     * 查询关注用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    @Override
    public List<ArticleVO> queryArticleOfAttention(Long userId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_ATTENTION, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<ArticleVO> articleVOList = gson.fromJson(redisData, new TypeToken<List<ArticleVO>>() {
        }.getType());

        // Redis 为空，查询数据库
        if (articleVOList == null || articleVOList.isEmpty()) {
            // 调用 cm-friend 模块，查询出我的关注用户
            List<FriendVO> attention = null;
            try {
                attention = friendFeignClient.attention(userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 动态集合
            List<ArticleVO> articleList = new ArrayList<>();
            if (attention != null && !attention.isEmpty()) {
                // 根据每一位用户的id查询出动态
                attention.forEach(friendVO -> {
                    // 动态发表者相关信息
                    ArticleUserVO publishUser = new ArticleUserVO();
                    BeanUtils.copyProperties(friendVO, publishUser);

                    // 获取动态id、动态内容、动态图片、动态发布时间
                    List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                            .select("article_id", "article_content", "article_photos", "create_time")
                            .eq("user_id", friendVO.getUserId()));

                    if (articles != null && !articles.isEmpty()) {
                        // 遍历 articles
                        articles.forEach(article -> {
                            // 转换动态信息
                            ArticleVO articleVO = new ArticleVO();
                            BeanUtils.copyProperties(article, articleVO);
                            // 根据动态id查询点赞信息
                            ArticleLikeVO articleLikeVO = likeFeignClient.queryLikeInfo(article.getArticleId());
                            // 根据动态id查询动态评论用户
                            ArticleCommentVO articleCommentVO = commentFeignClient.queryCommentInfo(article.getArticleId());
                            // 封装数据
                            articleVO.setPublishUser(publishUser);
                            articleVO.setLike(articleLikeVO);
                            articleVO.setComment(articleCommentVO);
                            articleList.add(articleVO);
                        });
                    }
                });
            }
            // 缓存数据
            redisUtil.setRedisData(redisKey, gson.toJson(articleList));
            return articleList;
        }
        return articleVOList;
    }

    /**
     * 查询校园所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    @Override
    public List<ArticleVO> queryArticleOfSchool(Long userId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_SCHOOL, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<ArticleVO> articleVOList = gson.fromJson(redisData, new TypeToken<List<ArticleVO>>() {
        }.getType());

        // Redis 为空，查询数据库
        if (articleVOList == null || articleVOList.isEmpty()) {
            // 查询我的关注用户动态信息
            List<ArticleVO> articleVOList1 = queryArticleOfAttention(userId);
            // 获取我的关注用户发布的动态的id
            List<Long> articleIdList = articleVOList1.stream().map(ArticleVO::getArticleId).collect(Collectors.toList());
            // 查询我自己发布的动态
            List<ArticleVO> articleVOList2 = queryArticleByUserId(userId);
            // 获取我自己的动态id
            List<Long> collect = articleVOList2.stream().map(ArticleVO::getArticleId).collect(Collectors.toList());
            // 加上自己的动态id集合
            articleIdList.addAll(collect);
            List<Article> articles;
            // 查询校园动态
            if (!articleIdList.isEmpty()) {
                articles = articleMapper.selectList(new QueryWrapper<Article>()
                        .select("article_id", "article_content", "article_photos", "create_time", "user_id")
                        .notIn("article_id", articleIdList));
            } else {
                articles = articleMapper.selectList(new QueryWrapper<Article>()
                        .select("article_id", "article_content", "article_photos", "create_time", "user_id"));
            }
            // 动态集合
            List<ArticleVO> articleList = new ArrayList<>();
            if (articles != null && !articles.isEmpty()) {
                // 遍历动态
                articles.forEach(article -> {
                    // 查询动态发布用户信息
                    UserInfoVO userInfoVO = userFeignClient.getUserInfo(article.getUserId());
                    // 动态发表者相关信息
                    ArticleUserVO publishUser = new ArticleUserVO();
                    BeanUtils.copyProperties(userInfoVO, publishUser);
                    // 转换动态信息
                    ArticleVO articleVO = new ArticleVO();
                    BeanUtils.copyProperties(article, articleVO);
                    // 根据动态id查询点赞信息
                    ArticleLikeVO articleLikeVO = likeFeignClient.queryLikeInfo(article.getArticleId());
                    // 根据动态id查询动态评论用户
                    ArticleCommentVO articleCommentVO = commentFeignClient.queryCommentInfo(article.getArticleId());
                    // 封装数据
                    articleVO.setPublishUser(publishUser);
                    articleVO.setLike(articleLikeVO);
                    articleVO.setComment(articleCommentVO);
                    articleList.add(articleVO);
                });
            }
            // 缓存数据
            redisUtil.setRedisData(redisKey, gson.toJson(articleList));
            return articleList;
        }
        return articleVOList;
    }
}
