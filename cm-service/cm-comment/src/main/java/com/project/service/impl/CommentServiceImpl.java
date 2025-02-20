package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.CommentVO;
import com.project.VO.FriendVO;
import com.project.VO.UserVO;
import com.project.api.UserFeignClient;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Comment;
import com.project.mapper.CommentMapper;
import com.project.service.CommentService;
import com.project.util.RedisUtil;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private RedisUtil redisUtil;
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
        String fromRedis = redisUtil.getFromRedis(RedisKeyConstants.ARTICLE_COMMENT, articleId);
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
            redisUtil.saveIntoRedis(RedisKeyConstants.ARTICLE_COMMENT, articleId, gson.toJson(list));
            return list;
        }

        return commentVOList;
    }
}
