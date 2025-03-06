package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.VO.lost.LostListVO;
import com.project.VO.lost.LostVO;
import com.project.common.ResultCodeEnum;
import com.project.domain.Lost;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.LostMapper;
import com.project.service.LostService;
import com.project.util.UploadAvatar;
import com.project.util.UserContext;
import com.project.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class LostServiceImpl extends ServiceImpl<LostMapper, Lost>
        implements LostService {
    @Resource
    private LostMapper lostMapper;

    /**
     * 发布失物招领信息
     * 请求数据
     * - lostType 类型
     * - lostName 名称
     * - lostDescription 描述
     * - file 图片文件
     */
    @Override
    public boolean publish(String lostType, String lostName, String lostDescription, MultipartFile file) {
        // 验证参数
        if (StringUtils.isAnyBlank(lostType, lostName, lostDescription)) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }
        if (file == null) {
            throw new BusinessExceptionHandler(Objects.requireNonNull(ResultCodeEnum.getByCode(400)));
        }

        // 图片上传
        try {
            String link = UploadAvatar.uploadAvatar(file, "lost");
            Lost lost = new Lost();
            lost.setUserId(UserContext.getUserId());
            lost.setLostType(lostType);
            lost.setLostName(lostName);
            lost.setLostDescription(lostDescription);
            lost.setLostPhoto(link);
            int insert = lostMapper.insert(lost);
            if (insert == 0) {
                log.warn("失物招领模块，数据插入失败");
                return false;
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询我的失物招领数据
     */
    @Override
    public List<LostVO> queryMy(Long userId) {
        // 判断是我自己查询还是其他用户查询
        userId = ValidateUtil.validateUserId(userId);

        // 查询数据库记录
        List<Lost> losts = lostMapper.selectList(new QueryWrapper<Lost>()
                .select("id", "lost_type", "lost_name", "lost_description", "lost_photo", "create_time")
                .eq("user_id", userId)
                .orderByDesc("create_time"));

        List<LostVO> list = new ArrayList<>();
        losts.forEach(lost -> {
            LostVO lostVO = new LostVO();
            lostVO.setId(lost.getId());
            lostVO.setLostType(lost.getLostType());
            lostVO.setLostName(lost.getLostName());
            lostVO.setLostDescription(lost.getLostDescription());
            lostVO.setLostPhoto(lost.getLostPhoto());
            lostVO.setCreateTime(lost.getCreateTime());
            list.add(lostVO);
        });

        return list;
    }

    /**
     * 删除我的失物招领数据
     */
    @Override
    public boolean deleteMy(Long lostId) {
        // 验证参数
        ValidateUtil.validateSingleLongTypeParam(lostId);

        // 删除数据库记录
        int i = lostMapper.deleteById(lostId);
        if (i == 0) {
            log.warn("失物招领模块，删除数据失败");
            return false;
        }

        return true;
    }

    /**
     * 查询所有失物招领数据
     */
    @Override
    public List<LostListVO> queryAll() {
        // 获取我的id
        Long userId = UserContext.getUserId();

        // 查询数据库
        List<LostListVO> list = lostMapper.queryAll();

        return list;
    }
}
