package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.ArticleVO;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Article;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.ArticleMapper;
import com.project.service.ArticleService;
import com.project.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
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
        String listStr = redisTemplate.opsForValue().get(RedisKeyConstants.getUserArticleKey(userId));
        List<ArticleVO> list = gson.fromJson(listStr, new TypeToken<List<ArticleVO>>() {
        }.getType());

        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .select("article_id", "article_content", "article_photos", "create_time")
                    .eq("user_id", userId));
            if (articles == null || articles.isEmpty()) {
                return null;
            } else {
                List<ArticleVO> collect = articles.stream().map(article -> {
                    ArticleVO articleVO = new ArticleVO();
                    BeanUtils.copyProperties(article, articleVO);
                    return articleVO;
                }).collect(Collectors.toList());
                // 存入redis
                redisTemplate.opsForValue().set(RedisKeyConstants.getUserArticleKey(userId), gson.toJson(collect));
                return collect;
            }
        }

        return list;
    }

    private Long getUserId(Long id) {
        // id存在，查询别人的信息
        // id不存在，查询自己的信息
        return id == null ? UserContext.getUserId() : id;
    }
}
