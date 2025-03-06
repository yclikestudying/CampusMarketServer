package com.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.VO.lost.LostListVO;
import com.project.domain.Lost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LostMapper extends BaseMapper<Lost> {
    // 查询所有失物招领数据
    List<LostListVO> queryAll();
}
