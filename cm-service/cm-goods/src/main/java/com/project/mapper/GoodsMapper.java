package com.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.VO.Goods.GoodsListVO;
import com.project.domain.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {
    // 查询所有商品(排除自己的)
    List<GoodsListVO> queryAllGoods(@Param("userId") Long userId);
}
