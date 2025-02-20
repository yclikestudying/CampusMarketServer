package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.ArticleVO;
import com.project.VO.FriendVO;
import com.project.api.FriendFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Article;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.ArticleMapper;
import com.project.service.ArticleService;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private FriendFeignClient friendFeignClient;
    private final Gson gson = new Gson();

    /**
     * 根据用户id查询动态
     *
     * @param id
     * @return
     */
    @Override
    public List<ArticleVO> queryArticleByUserId(Long id) {
        // 验证
        Long userId = getUserId(id);
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询redis
        String listStr = redisTemplate.opsForValue().get(RedisKeyConstants.getArticleUserKey(userId));
        List<ArticleVO> list = gson.fromJson(listStr, new TypeToken<List<ArticleVO>>() {
        }.getType());

        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .select("article_id", "article_content", "article_photos", "create_time")
                    .eq("user_id", userId));
            if (articles == null || articles.isEmpty()) {
                return null;
            }
            List<ArticleVO> collect = articles.stream().map(article -> {
                ArticleVO articleVO = new ArticleVO();
                BeanUtils.copyProperties(article, articleVO);
                return articleVO;
            }).collect(Collectors.toList());
            // 存入redis
            redisTemplate.opsForValue().set(RedisKeyConstants.getArticleUserKey(userId), gson.toJson(collect));
            return collect;
        }

        return list;
    }

    /**
     * 查询校园动态（不包括关注的用户的动态）
     *
     * @return
     */
    @Override
    public List<ArticleVO> queryArticle(Long userId) {
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询redis
        String listStr = redisTemplate.opsForValue().get(RedisKeyConstants.getArticleSchoolKey(userId));
        List<ArticleVO> articleVOList = gson.fromJson(listStr, new TypeToken<List<ArticleVO>>() {
        }.getType());

        if (articleVOList == null || articleVOList.isEmpty()) {
            // 查询数据库
            // 调用 cm-friend 模块，获取到关注的用户id
            List<FriendVO> list = null;
            try {
                list = friendFeignClient.attentionApi(userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<Long> collect = list.stream().map(FriendVO::getUserId).collect(Collectors.toList());
            collect.add(userId);

            // 查询动态，把关注的用户和本身的动态排除在外
            List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .select("article_id", "article_content", "article_photos", "create_time")
                    .notIn("user_id", collect));
            if (articles == null || articles.isEmpty()) {
                return null;
            }
            // 数据脱敏
            List<ArticleVO> voList = articles.stream().map(article -> {
                ArticleVO articleVO = new ArticleVO();
                BeanUtils.copyProperties(article, articleVO);
                return articleVO;
            }).collect(Collectors.toList());
            // 存入redis
            redisTemplate.opsForValue().set(RedisKeyConstants.getArticleSchoolKey(userId), gson.toJson(voList));
            return voList;
        }

        return articleVOList;
    }

    /**
     * 查询关注用户的动态
     *
     * @return
     */
    @Override
    public List<ArticleVO> queryArticleByAttention(Long userId) {
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
        // 查询redis
        String listStr = redisTemplate.opsForValue().get(RedisKeyConstants.getArticleAttentionKey(userId));
        List<ArticleVO> list = gson.fromJson(listStr, new TypeToken<List<ArticleVO>>() {
        }.getType());

        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            // 调用 cm-friend 模块，获取关注用户id
            List<FriendVO> friendVOList = friendFeignClient.attentionApi(userId);
            List<Long> ids = friendVOList.stream().map(FriendVO::getUserId).collect(Collectors.toList());
            // 根据id批量查询动态
            List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .select("article_id", "article_content", "article_photos", "create_time")
                    .in("user_id", ids));

            if (articles == null || articles.isEmpty()) {
                // 数据库为空
                return null;
            }

            // 数据脱敏
            List<ArticleVO> collect = articles.stream().map(article -> {
                ArticleVO articleVO = new ArticleVO();
                BeanUtils.copyProperties(article, articleVO);
                return articleVO;
            }).collect(Collectors.toList());
            // 存入redis
            redisTemplate.opsForValue().set(RedisKeyConstants.getArticleAttentionKey(userId), gson.toJson(collect));
            return collect;
        }

        return list;
    }

    /**
     * 根据动态id删除动态
     *
     * @param articleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class) // 确保所有异常都会触发回滚
    @Override
    public boolean deleteByArticleId(Long articleId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(articleId);

        // 删除数据库记录
        int deletedRows = articleMapper.deleteById(articleId);
        if (deletedRows == 0) {
            log.warn("id为{}的文章不存在", articleId);
            return false;
        }

        // 删除 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ARTICLE_USER, UserContext.getUserId());
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

    private Long getUserId(Long id) {
        // id存在，查询别人的信息
        // id不存在，查询自己的信息
        return id == null ? UserContext.getUserId() : id;
    }
}
