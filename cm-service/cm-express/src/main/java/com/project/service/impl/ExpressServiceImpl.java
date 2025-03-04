package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.DTO.ExpressDTO;
import com.project.VO.express.ExpressListVO;
import com.project.VO.express.ExpressVO;
import com.project.domain.Express;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.ExpressMapper;
import com.project.service.ExpressService;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExpressServiceImpl extends ServiceImpl<ExpressMapper, Express>
        implements ExpressService {
    @Resource
    private ExpressMapper expressMapper;
    @Resource
    private ThreadPoolTaskExecutor executor;

    /**
     * 跑腿内容上传
     * 请求数据
     * - content 文本内容
     * - price 价格
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean upload(ExpressDTO expressDTO) {
        // 获取参数
        String content = expressDTO.getContent();
        String price = expressDTO.getPrice();

        // 验证参数
        if (StringUtils.isAnyBlank(content, price)) {
            throw new BusinessExceptionHandler(400, "参数为空");
        }

        // 保存数据库
        Express express = new Express();
        express.setUserId(UserContext.getUserId());
        express.setExpressContent(content);
        express.setExpressPrice(Integer.parseInt(price));
        int insert = expressMapper.insert(express);

        return insert > 0;
    }

    /**
     * 查询自己发布的跑腿服务
     * 请求数据
     * - userId 用户id
     */
    @Override
    public List<ExpressVO> queryMyExpress(Long userId) {
        // 验证是查询自己的还是其他用户的
        userId = ValidateUtil.validateUserId(userId);

        // 查询数据库记录
        List<Express> expresses = expressMapper.selectList(new QueryWrapper<Express>()
                .select("id", "express_content", "express_price", "create_time")
                .eq("user_id", userId)
                .orderByDesc("create_time"));

        // 对数据进行脱敏
        // 并发进行处理
        if (!expresses.isEmpty()) {
            List<CompletableFuture<ExpressVO>> futures = expresses.stream().map(express -> CompletableFuture.supplyAsync(() -> {
                ExpressVO expressVO = new ExpressVO();
                expressVO.setId(express.getId());
                expressVO.setExpressContent(express.getExpressContent());
                expressVO.setExpressPrice(express.getExpressPrice());
                expressVO.setCreateTime(express.getCreateTime());
                return expressVO;
            }, executor)).collect(Collectors.toList());

            List<ExpressVO> expressVOList = futures
                    .stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            return expressVOList;
        }

        return null;
    }

    /**
     * 查询所有的跑腿服务（排除自己的）
     */
    @Override
    public List<ExpressListVO> queryAllExpress() {
        // 获取自己的用户id
        Long userId = UserContext.getUserId();

        // 查询数据库记录
        List<ExpressListVO> expressListVOS = expressMapper.queryAllExpress();

        // 排除自己
        List<ExpressListVO> collect = expressListVOS
                .stream()
                .filter(expressListVO -> !Objects.equals(expressListVO.getUserId(), userId))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * 删除自己的跑腿服务
     * 请求数据
     * - expressId 跑腿服务id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteExpress(Long expressId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(expressId);

        // 删除数据库记录
        int i = expressMapper.deleteById(expressId);
        if (i == 0) {
            log.warn("跑腿服务删除失败");
            return false;
        }

        return true;
    }
}
