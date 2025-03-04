package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.Goods.GoodsListVO;
import com.project.VO.Goods.GoodsVO;
import com.project.domain.Goods;

import java.util.List;
import java.util.Map;

public interface GoodsService extends IService<Goods> {
    /**
     * 商品上传
     */
    boolean uploadGoods(Map<Integer, Object> map, String text, String price);

    /**
     * 查询自己发布的商品
     * 请求数据
     * - userId 用户id
     */
    List<GoodsVO> queryGoods(Long userId);

    /**
     * 根据商品id删除商品
     * 请求数据
     * - goodsId 商品id
     */
    boolean deleteByGoodsId(Long goodsId);

    /**
     * 查询所有商品（排除自己的）
     */
    List<GoodsListVO> queryAllGoods();

}
