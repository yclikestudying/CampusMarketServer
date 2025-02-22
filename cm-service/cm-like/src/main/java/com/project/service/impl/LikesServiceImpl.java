package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.article.ArticleLikeVO;
import com.project.VO.article.ArticleUserVO;
import com.project.VO.article.ArticleVO;
import com.project.api.ArticleFeignClient;
import com.project.api.UserFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Likes;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.LikesMapper;
import com.project.service.LikesService;
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
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private UserFeignClient userFeignClient; // 调用 cm-user 模块
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
        List<Long> userIdList = null;
        if (likeList != null && !likeList.isEmpty()) {
            userIdList = likeList.stream().map(Likes::getUserId).collect(Collectors.toList());
            // 批量查询用户信息
            List<ArticleUserVO> articleUserVOList = userFeignClient.getUserInfoBatch(userIdList);

            ArticleLikeVO articleLikeVO = new ArticleLikeVO();
            articleLikeVO.setCount(articleUserVOList.size());
            articleLikeVO.setArticleUserVOList(articleUserVOList);
            return articleLikeVO;
        }
        return null;
    }

    /**
     * 根据动态id进行点赞
     *
     * @param userId
     * @param articleId
     */
    @Override
    public String like(Long userId, Long articleId) {
        validateUserId(userId);
        if (articleId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询点赞记录
        Likes likes = likesMapper.selectOne(new QueryWrapper<Likes>()
                .eq("article_id", articleId)
                .eq("user_id", userId));
        if (likes != null) {
            return "不能重复点赞";
        }

        // 保存点赞信息
        Likes one = new Likes();
        one.setArticleId(articleId);
        one.setUserId(userId);

        return likesMapper.insert(one) > 0 ? "点赞成功" : "点赞失败";
    }

    @Override
    public String unlike(Long userId, Long articleId) {
        validateUserId(userId);
        if (articleId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询点赞记录
        Likes likes = likesMapper.selectOne(new QueryWrapper<Likes>()
                .eq("article_id", articleId)
                .eq("user_id", userId));
        if (likes == null) {
            return "不能取消点赞";
        }

        // 删除点赞信息
        int delete = likesMapper.delete(new QueryWrapper<Likes>()
                .eq("article_id", articleId)
                .eq("user_id", userId));

        return delete > 0 ? "取消点赞成功" : "取消点赞失败";
    }

    /**
     * 根据远程调用获取的文章查询其点赞
     *
     * @param list
     * @return
     */
    private Map<Long, Integer> getLikeMap(List<ArticleVO> list) {
        // 发布过动态就获取动态id
        List<Long> collect = list.stream().map(ArticleVO::getArticleId).collect(Collectors.toList());
        // 根据每一个动态id查询点赞数
        Map<Long, Integer> map = new HashMap<>();
        collect.forEach(articleId -> {
            Integer count = likesMapper.selectCount(new QueryWrapper<Likes>().eq("article_id", articleId));
            map.put(articleId, count);
        });
        return map;
    }

    /**
     * 把数据存入 redis 中
     *
     * @param str
     * @param userId
     * @param likeMap
     */
    private void saveRedis(String str, Long userId, Map<Long, Integer> likeMap) {
        redisTemplate.opsForValue().set(RedisKeyConstants.getRedisKey(str, userId), gson.toJson(likeMap));
    }

    /**
     * 获取 redis 中的数据并反序列化
     *
     * @param str
     * @param userId
     * @return
     */
    private Map<Long, Integer> getRedisLikeMap(String str, Long userId) {
        String likeMapStr = redisTemplate.opsForValue().get(RedisKeyConstants.getRedisKey(str, userId));
        return gson.fromJson(likeMapStr, new TypeToken<Map<Long, Integer>>() {
        }.getType());
    }

    /**
     * 验证 userId
     *
     * @param userId
     */
    private void validateUserId(Long userId) {
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
    }

    /**
     * 判断查询哪一个用户的信息
     *
     * @param id
     * @return
     */
    private Long getUserId(Long id) {
        // id存在，说明前端传值，查询别人的信息
        // id不存在，说明前端未传值，查询自己的信息
        return id == null ? UserContext.getUserId() : id;
    }
}
