package com.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.VO.VisitVO;
import com.project.domain.Visit;

import java.util.List;

public interface VisitMapper extends BaseMapper<Visit> {
    /**
     * 查询访客记录
     */
    List<VisitVO> selectVisitUser(Long userId);

}
