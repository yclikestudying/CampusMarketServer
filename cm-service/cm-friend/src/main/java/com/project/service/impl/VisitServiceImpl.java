package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.VO.FriendVO;
import com.project.VO.VisitVO;
import com.project.api.UserFeignClient;
import com.project.constants.RedisKeyConstants;
import com.project.domain.Visit;
import com.project.mapper.VisitMapper;
import com.project.service.VisitService;
import com.project.util.RedisUtil;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VisitServiceImpl extends ServiceImpl<VisitMapper, Visit>
        implements VisitService {
    @Resource
    private VisitMapper visitMapper;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private RedisUtil redisUtil;
    private final Gson gson = new Gson();

    /**
     * 记录访客记录
     * 请求数据
     * - visitedId 被访问者id
     */
    @Override
    public boolean visit(Long visitedId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(visitedId);
        Long userId = UserContext.getUserId();

        // 查询是否已有同一用户的访客记录
        Visit one = visitMapper.selectOne(new QueryWrapper<Visit>()
                .eq("visitor_id", userId)
                .eq("visited_id", visitedId));
        if (one != null) {
            // 删除旧访问记录
            int i = visitMapper.deleteById(one.getId());
            if (i == 0) {
                log.warn("删除旧访客记录失败");
                return false;
            }
        }

        // 插入新的访客记录
        Visit visit = new Visit();
        visit.setVisitorId(userId);
        visit.setVisitedId(visitedId);

        // 删除 Redis 记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_VISIT, userId);
        redisUtil.redisTransaction(redisKey);
        return visitMapper.insert(visit) > 0;
    }

    /**
     * 查询访客记录
     */
    @Override
    public List<VisitVO> queryVisit(Long userId) {
        // 从 Redis 中查询访客记录
        String redisKey = RedisKeyConstants.getRedisKey(RedisKeyConstants.USER_VISIT, userId);
        String redisData = redisUtil.getRedisData(redisKey);
        List<VisitVO> visitList = gson.fromJson(redisData, new TypeToken<List<VisitVO>>() {
        }.getType());

        if (visitList == null || visitList.isEmpty()) {

            // 查询访问我的用户 联表查询 user visit
            List<VisitVO> visitVOS = visitMapper.selectVisitUser(userId);

            // 更新 Redis 缓存
            redisUtil.setRedisData(redisKey, gson.toJson(visitVOS));
            return visitVOS;
        }
       return visitList;
    }
}
