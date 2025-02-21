package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.FriendVO;
import com.project.api.UserFeignClient;
import com.project.common.ResultCodeEnum;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Friends;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.FriendsMapper;
import com.project.service.FriendsService;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
        implements FriendsService {
    @Resource
    private FriendsMapper friendsMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private UserFeignClient userFeignClient;
    private final Gson gson = new Gson();

    /**
     * 查询我的关注
     *
     * @param id 用户id
     * @return
     */
    @Override
    public List<FriendVO> attention(Long id) {
        // 验证id
        Long userId = getUserId(id);
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询redis
        String attention = redisTemplate.opsForValue().get(RedisKeyConstants.getUserAttentionKey(userId));
        List<FriendVO> list = gson.fromJson(attention, new TypeToken<List<FriendVO>>() {
        }.getType());
        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            List<Friends> friendsList = friendsMapper.selectList(new QueryWrapper<Friends>().eq("follower_id", userId));
            if (friendsList == null || friendsList.isEmpty()) {
                return null;
            } else {
                // 调用 cm-user 模块，获取用户信息
                List<FriendVO> friendList = null;
                try {
                    friendList = userFeignClient.getUserInfoApi(friendsList.stream().map(Friends::getFolloweeId).collect(Collectors.toList()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // 存入redis
                redisTemplate.opsForValue().set(RedisKeyConstants.getUserAttentionKey(userId), gson.toJson(friendList));
                return friendList;
            }
        }

        return list;
    }

    /**
     * 查询我的粉丝
     *
     * @param id
     * @return
     */
    @Override
    public List<FriendVO> fans(Long id) {
        // 验证
        Long userId = getUserId(id);
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询redis
        String fans = redisTemplate.opsForValue().get(RedisKeyConstants.getUserFansKey(userId));
        List<FriendVO> list = gson.fromJson(fans, new TypeToken<List<FriendVO>>() {
        }.getType());

        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            List<Friends> friendsList = friendsMapper.selectList(new QueryWrapper<Friends>().eq("followee_id", userId));
            if (friendsList == null || friendsList.isEmpty()) {
                return null;
            } else {
                // 调用 cm-user 模块，获取用户信息
                List<FriendVO> friendVOList = userFeignClient.getUserInfoApi(friendsList.stream().map(Friends::getFollowerId).collect(Collectors.toList()));
                // 存入redis
                redisTemplate.opsForValue().set(RedisKeyConstants.getUserFansKey(userId), gson.toJson(friendVOList));
                return friendVOList;
            }
        }

        return list;
    }

    /**
     * 查询互关用户
     *
     * @param id
     * @return
     */
    @Override
    public List<FriendVO> attentionAndFans(Long id) {
        // 验证
        Long userId = getUserId(id);
        if (userId <= 0) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 查询redis
        String listStr = redisTemplate.opsForValue().get(RedisKeyConstants.getUserAttentionAndFansKey(userId));
        List<FriendVO> list = gson.fromJson(listStr, new TypeToken<List<FriendVO>>() {
        }.getType());

        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            // 查询我的关注
            List<FriendVO> attentionList = attention(userId);
            List<FriendVO> fansList = fans(userId);
            // 获取交集
            attentionList.retainAll(fansList);
            // 调用 cm-user 模块，获取用户信息
            List<FriendVO> friendVOList = userFeignClient.getUserInfoApi(attentionList.stream().map(FriendVO::getUserId).collect(Collectors.toList()));
            // 存入redis
            redisTemplate.opsForValue().set(RedisKeyConstants.getUserAttentionAndFansKey(userId), gson.toJson(friendVOList));
            return friendVOList;
        }

        return list;
    }

    /**
     * 查询我的关注数量
     *
     * @param id
     * @return
     */
    @Override
    public Integer attentionCount(Long id) {
        Long userId = getUserId(id);
        // 验证
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ATTENTION_COUNT, userId);
        String str = redisTemplate.opsForValue().get(redisKey);
        Integer count = gson.fromJson(str, Integer.class);
        if (count == null) {
            // Redis 为空，查询数据库
            Integer friendCount = friendsMapper.selectCount(new QueryWrapper<Friends>().eq("follower_id", userId));
            // 存入 Redis
            redisTemplate.opsForValue().set(redisKey, gson.toJson(friendCount), 24, TimeUnit.HOURS);
            return friendCount;
        }

        return count;
    }

    /**
     * 查询我的粉丝数量
     *
     * @param id
     * @return
     */
    @Override
    public Integer fansCount(Long id) {
        Long userId = getUserId(id);
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.FANS_COUNT, userId);
        String str = redisTemplate.opsForValue().get(redisKey);
        Integer count = gson.fromJson(str, Integer.class);
        if (count == null) {
            // Redis 为空，查询数据库
            Integer friendCount = friendsMapper.selectCount(new QueryWrapper<Friends>().eq("followee_id", userId));
            // 存入 Redis
            redisTemplate.opsForValue().set(redisKey, gson.toJson(friendCount), 24, TimeUnit.HOURS);
            return friendCount;
        }

        return count;
    }

    /**
     * 查询互关用户数量
     *
     * @param userId
     * @return
     */
    @Override
    public Integer attentionAndFansCount(Long userId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.ATTENTIONFANS_COUNT, userId);
        String str = redisTemplate.opsForValue().get(redisKey);
        Integer count = gson.fromJson(str, Integer.class);
        if (count == null) {
            // Redis 为空，查询数据库
            // 查询我的关注用户
            List<Friends> friendsList = friendsMapper.selectList(new QueryWrapper<Friends>().select("followee_id").eq("follower_id", userId));
            List<Long> collect = null;
            if (friendsList != null && !friendsList.isEmpty()) {
                collect = friendsList.stream().map(Friends::getFolloweeId).collect(Collectors.toList());
            }
            // 查询我的粉丝
            List<Friends> friendsList1 = friendsMapper.selectList(new QueryWrapper<Friends>().select("follower_id").eq("followee_id", userId));
            List<Long> collect1 = null;
            if (friendsList1 != null && !friendsList1.isEmpty()) {
                collect1 = friendsList1.stream().map(Friends::getFollowerId).collect(Collectors.toList());
            }
            // 交集
            Integer attentionFansCount = null;
            if (collect == null || collect1 == null) {
                attentionFansCount = 0;
            } else {
                collect.retainAll(collect1);
                attentionFansCount = collect.size();
            }
            // 存入 Redis
            redisTemplate.opsForValue().set(redisKey, gson.toJson(attentionFansCount), 24, TimeUnit.HOURS);
            return attentionFansCount;
        }
        return count;
    }

    private Long getUserId(Long id) {
        // id存在，查询别人的信息
        // id不存在，查询自己的信息
        return id == null ? UserContext.getUserId() : id;
    }
}
