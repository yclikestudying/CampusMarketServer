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
import com.project.util.UploadAvatar;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
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
                    .eq("user_id", userId)
                    .orderByDesc("create_time"));

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

    /**
     * 查询用户动态数量
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - count 动态数量
     */
    @Override
    public Integer queryArticleCount(Long userId) {
        // 验证
        ValidateUtil.validateUserId(userId);

        // 查询数据库记录
        return articleMapper.selectCount(new QueryWrapper<Article>().eq("user_id", userId));
    }

    /**
     * 根据动态id删除动态以及相关信息
     * 请求数据:
     * - articleId 动态id
     */
    @Override
    public boolean deleteArticleByArticleId(Long articleId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(articleId);

        // 删除数据库动态
        int delete = articleMapper.deleteById(articleId);
        if (delete == 0) {
            log.warn("动态不存在，articleId: {}", articleId);
            return false;
        }

        // 删除相关点赞
        try {
            int likeResult = likeFeignClient.deleteLikeInfo(articleId);
            if (likeResult == 0) {
                log.warn("该动态没有点赞信息，articleId: {}", articleId);
            }
        } catch (Exception e) {
            log.error("删除点赞信息失败，articleId: {}", articleId, e);
        }

        // 删除相关评论
        try {
            int commentResult = commentFeignClient.deleteCommentInfo(articleId);
            if (commentResult == 0) {
                log.warn("该动态没有评论信息，articleId: {}", articleId);
            }
        } catch (Exception e) {
            log.error("删除评论信息失败，articleId: {}", articleId, e);
        }

        // 删除 Redis 缓存
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, UserContext.getUserId());
        redisUtil.redisTransaction(redisKey);
        return true;
    }

    /**
     * 上传校园动态
     * 请求数据:
     * - file 图片二进制数据
     * - text 文本内容
     * - count 图片数量
     */
    @Override
    public boolean uploadArticle(List<MultipartFile> files, String text) {
        // 验证参数
        if (files.isEmpty() && StringUtils.isBlank(text)) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        Article article = new Article();
        article.setUserId(UserContext.getUserId());
        List<String> link = new ArrayList<>();
        // 上传文件到oss
        if (!files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    String articleLink = UploadAvatar.uploadAvatar(file, "article");
                    link.add(articleLink);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            String linkStr = gson.toJson(link);
            article.setArticlePhotos(linkStr);
        }

        if (!StringUtils.isBlank(text)) {
            article.setArticleContent(text);
        }

        // 存储数据库
        int insert = articleMapper.insert(article);
        if (insert == 0) {
            log.warn("保存数据库失败");
            return false;
        }

        // 删除 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, UserContext.getUserId());
        redisUtil.redisTransaction(redisKey);
        return true;
    }
}
