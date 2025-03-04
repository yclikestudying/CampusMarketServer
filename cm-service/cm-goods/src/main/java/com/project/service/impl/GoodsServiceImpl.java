package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.project.VO.Goods.GoodsListVO;
import com.project.VO.Goods.GoodsVO;
import com.project.common.ResultCodeEnum;
import com.project.domain.Goods;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.GoodsMapper;
import com.project.service.GoodsService;
import com.project.util.UploadAvatar;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
        implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ThreadPoolTaskExecutor executor;
    private final Gson gson = new Gson();

    /**
     * 商品上传
     */
    @Override
    public boolean uploadGoods(Map<Integer, Object> map, String text, String price) {
        // 验证
        if (map.isEmpty() && StringUtils.isBlank(text)) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 上传阿里云oos
        if (!map.isEmpty()) {
            for (Map.Entry<Integer, Object> entry : map.entrySet()) {
                Integer key = entry.getKey();
                MultipartFile file = (MultipartFile) entry.getValue();

                // 并发上传
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return UploadAvatar.uploadAvatar(file, "goods");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

                try {
                    // 阻塞当前线程，等所有线程全部完成，才获取到结果
                    map.put(key, future.join());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // 获取新地址到list集合中
        List<Object> list = new ArrayList<>(map.values());
        Goods goods = new Goods();
        goods.setUserId(UserContext.getUserId());
        goods.setGoodsContent(text);
        goods.setGoodsPhotos(gson.toJson(list));
        goods.setGoodsPrice(Integer.parseInt(price));
        int insert = goodsMapper.insert(goods);

        return insert > 0;
    }

    /**
     * 查询自己发布的商品
     * 请求数据
     * - userId 用户id
     */
    @Override
    public List<GoodsVO> queryGoods(Long userId) {
        // 查询是自己的id还是其他用户的id
        userId = ValidateUtil.validateUserId(userId);

        // 查询数据库商品记录
        List<Goods> goodsList = goodsMapper.selectList(new QueryWrapper<Goods>()
                .select("id", "goods_content", "goods_photos", "goods_price", "create_time")
                .eq("user_id", userId)
                .orderByDesc("create_time"));

        // 并发处理进行脱敏
        List<CompletableFuture<GoodsVO>> future = goodsList.stream().map(goods -> CompletableFuture.supplyAsync(() -> {
            GoodsVO goodsVO = new GoodsVO();
            BeanUtils.copyProperties(goods, goodsVO);
            return goodsVO;
        }, executor)).collect(Collectors.toList());

        return future.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * 根据商品id删除商品
     * 请求数据
     * - goodsId 商品id
     */
    @Override
    public boolean deleteByGoodsId(Long goodsId) {
        // 验证
        ValidateUtil.validateSingleLongTypeParam(goodsId);

        // 删除数据库记录
        int i = goodsMapper.deleteById(goodsId);
        if (i == 0) {
            log.warn("商品删除失败");
            return false;
        }

        return true;
    }

    /**
     * 查询所有商品
     */
    @Override
    public List<GoodsListVO> queryAllGoods() {
        // 获取自己的id
        Long userId = UserContext.getUserId();

        // 查询所有商品
        return goodsMapper.queryAllGoods();
    }
}
