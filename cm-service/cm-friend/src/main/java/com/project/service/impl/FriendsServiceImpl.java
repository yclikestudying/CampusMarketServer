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
import com.project.util.RedisUtil;
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
    private UserFeignClient userFeignClient;
    @Resource
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

    /**
     * 查询我的关注
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    @Override
    public List<FriendVO> attention(Long id) {
        // 验证id
        Long userId = ValidateUtil.validateUserId(id);
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTION, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<FriendVO> list = gson.fromJson(redisData, new TypeToken<List<FriendVO>>() {
        }.getType());
        if (list == null || list.isEmpty()) {
            // Redis 为空，查询数据库
            List<Friends> friendsList = friendsMapper.selectList(new QueryWrapper<Friends>().eq("follower_id", userId));
            if (friendsList == null || friendsList.isEmpty()) {
                return null;
            }
            // 调用 cm-user 模块，获取用户信息
            List<FriendVO> friendList = null;
            try {
                friendList = userFeignClient.getUserBatch(friendsList.stream().map(Friends::getFolloweeId).collect(Collectors.toList()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 存入redis
            redisUtil.setRedisData(redisKey, gson.toJson(friendList));
            return friendList;
        }

        return list;
    }

    /**
     * 查询我的粉丝
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    @Override
    public List<FriendVO> fans(Long id) {
        // 验证
        Long userId = ValidateUtil.validateUserId(id);
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询redis
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_FANS, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<FriendVO> list = gson.fromJson(redisData, new TypeToken<List<FriendVO>>() {
        }.getType());

        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            List<Friends> friendsList = friendsMapper.selectList(new QueryWrapper<Friends>().eq("followee_id", userId));
            if (friendsList == null || friendsList.isEmpty()) {
                return null;
            } else {
                // 调用 cm-user 模块，获取用户信息
                List<FriendVO> friendVOList = userFeignClient.getUserBatch(friendsList.stream().map(Friends::getFollowerId).collect(Collectors.toList()));
                // 存入redis
                redisUtil.setRedisData(redisKey, gson.toJson(friendVOList));
                return friendVOList;
            }
        }

        return list;
    }

    /**
     * 查询互关用户
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - List<FriendVO> 用户列表
     */
    @Override
    public List<FriendVO> attentionAndFans(Long id) {
        // 验证
        Long userId = ValidateUtil.validateUserId(id);
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询redis
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTIONFANS, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<FriendVO> list = gson.fromJson(redisData, new TypeToken<List<FriendVO>>() {
        }.getType());

        if (list == null || list.isEmpty()) {
            // redis为空，查询数据库
            // 查询我的关注
            List<FriendVO> attentionList = attention(userId);
            List<FriendVO> fansList = fans(userId);
            if (attentionList == null || fansList == null) {
                return null;
            }
            // 获取交集
            attentionList.retainAll(fansList);
            // 调用 cm-user 模块，获取用户信息
            List<FriendVO> friendVOList = userFeignClient.getUserBatch(attentionList.stream().map(FriendVO::getUserId).collect(Collectors.toList()));
            // 存入redis
            redisUtil.setRedisData(redisKey, gson.toJson(friendVOList));
            return friendVOList;
        }

        return list;
    }

    /**
     * 查询我的关注数量
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - count 关注数量
     */
    @Override
    public Integer attentionCount(Long id) {
        return null;
    }

    /**
     * 查询我的粉丝数量
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - count 粉丝数量
     */
    @Override
    public Integer fansCount(Long id) {
        Long userId = getUserId(id);
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询数据库记录
        return friendsMapper.selectCount(new QueryWrapper<Friends>().eq("followee_id", userId));
    }

    /**
     * 查询互关用户数量
     * 请求数据:
     * - userId 用户id
     * 响应数据
     * - count 互关用户数量
     */
    @Override
    public Integer attentionAndFansCount(Long userId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(userId);

        // 查询 Redis 记录
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
        return attentionFansCount;
    }

    /**
     * 关注用户
     * 请求数据
     * - userId 我的id
     * - otherId 关注用户的id
     */
    @Override
    public boolean attentionUser(Long userId, Long otherId) {
        // 验证参数
        ValidateUtil.validateTwoLongTypeParam(userId, otherId);

        // 查询数据库记录
        Friends one = friendsMapper.selectOne(new QueryWrapper<Friends>()
                .eq("follower_id", userId)
                .eq("followee_id", otherId));
        if (one != null) {
            log.warn("不能重复关注");
            return false;
        }

        // 存入数据库
        Friends friends = new Friends();
        friends.setFollowerId(userId);
        friends.setFolloweeId(otherId);
        int insert = friendsMapper.insert(friends);
        if (insert == 0) {
            log.warn("关注失败");
            return false;
        }

        // 更新缓存
        // 我的关注
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTION, userId));
        // 我的互关
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTIONFANS, userId));
        // 他的粉丝
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_FANS, otherId));
        // 他的互关
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTIONFANS, otherId));
        return true;
    }

    /**
     * 取消关注用户
     * 请求数据
     * - userId 我的id
     * - otherId 关注用户的id
     */
    @Override
    public boolean unAttentionUser(Long userId, Long otherId) {
        // 验证参数
        ValidateUtil.validateTwoLongTypeParam(userId, otherId);

        // 查询数据库记录
        Friends one = friendsMapper.selectOne(new QueryWrapper<Friends>()
                .eq("follower_id", userId)
                .eq("followee_id", otherId));
        if (one == null) {
            log.warn("还未关注，取消关注失败");
            return false;
        }

        // 删除数据库记录
        int i = friendsMapper.deleteById(one.getId());
        if (i == 0) {
            log.warn("删除失败");
            return false;
        }

        // 更新缓存
        // 我的关注
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTION, userId));
        // 我的互关
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTIONFANS, userId));
        // 他的粉丝
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_FANS, otherId));
        // 他的互关
        redisUtil.redisTransaction(RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_ATTENTIONFANS, otherId));
        return true;
    }

    private Long getUserId(Long id) {
        // id存在，查询别人的信息
        // id不存在，查询自己的信息
        return id == null ? UserContext.getUserId() : id;
    }
}
