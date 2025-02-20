package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.ArticleVO;
import com.project.VO.LikeVO;
import com.project.api.ArticleFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Likes;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.LikesMapper;
import com.project.service.LikesService;
import com.project.util.UserContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes>
        implements LikesService {
    @Resource
    private LikesMapper likesMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ArticleFeignClient articleFeignClient;
    private final Gson gson = new Gson();

    /**
     * 获取自己的动态的点赞
     *
     * @param id
     * @return
     */
    @Override
    public Map<Long, Integer> queryLikesOfUser(Long id) {
        Long userId = getUserId(id);
        validateUserId(userId);

        // 查询redis
        Map<Long, Integer> likeMap = getRedisLikeMap(RedisKeyConstants.LIKE_USER, userId);

        if (likeMap == null || likeMap.isEmpty()) {
            // redis为空，查询数据库
            // 调用 cm-article 模块，获取自己发布的动态id
            List<ArticleVO> list = null;
            try {
                list = articleFeignClient.queryArticleByUserIdApi(userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (list == null || list.isEmpty()) {
                // 该用户没有发布过动态
                return null;
            }
            // 获取点赞集合
            Map<Long, Integer> map = getLikeMap(list);
            // 存入redis
            saveRedis(RedisKeyConstants.LIKE_USER, userId, map);
            return map;
        }

        return likeMap;
    }

    /**
     * 获取校园动态的点赞
     *
     * @param userId
     * @return
     */
    @Override
    public Map<Long, Integer> queryLikesOfCampus(Long userId) {
        validateUserId(userId);

        // 查询redis
        Map<Long, Integer> likeMap = getRedisLikeMap(RedisKeyConstants.LIKE_SCHOOL, userId);

        if (likeMap == null || likeMap.isEmpty()) {
            // redis为空，查询数据库
            // 调用 cm-article 模块，获取校园动态的id
            List<ArticleVO> list = null;
            try {
                list = articleFeignClient.queryArticleApi();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (list == null || list.isEmpty()) {
                // 校园模块没有动态
                return null;
            }
            // 获取点赞集合
            Map<Long, Integer> map = getLikeMap(list);
            // 存入redis
            saveRedis(RedisKeyConstants.LIKE_SCHOOL, userId, map);
            return map;
        }

        return likeMap;
    }

    /**
     * 获取关注用户动态的点赞
     *
     * @param userId
     * @return
     */
    @Override
    public Map<Long, Integer> queryLikesOfAttention(Long userId) {
        validateUserId(userId);
        // 查询redis
        Map<Long, Integer> likeMap = getRedisLikeMap(RedisKeyConstants.LIKE_ATTENTION, userId);

        if (likeMap == null || likeMap.isEmpty()) {
            // redis为空，查询数据库
            // 调用 cm-article 模块，获取校园动态的id
            List<ArticleVO> list = null;
            try {
                list = articleFeignClient.queryArticleByAttentionApi();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (list == null || list.isEmpty()) {
                // 关注用户模块没有动态
                return null;
            }
            // 获取点赞集合
            Map<Long, Integer> map = getLikeMap(list);
            // 存入redis
            saveRedis(RedisKeyConstants.LIKE_ATTENTION, userId, map);
            return map;
        }

        return likeMap;
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
