package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.FriendVO;
import com.project.VO.VisitVO;
import com.project.domain.Visit;

import java.util.List;

public interface VisitService extends IService<Visit> {
    /**
     * 记录访客记录
     * 请求数据
     * - visitedId 被访问者id
     */
    boolean visit(Long visitedId);

    /**
     * 查询访客记录
     */
    List<VisitVO> queryVisit(Long userId);
}
