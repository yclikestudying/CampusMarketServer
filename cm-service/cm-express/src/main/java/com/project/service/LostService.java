package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.lost.LostListVO;
import com.project.VO.lost.LostVO;
import com.project.domain.Lost;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LostService extends IService<Lost> {
    /**
     * 发布失物招领信息
     * 请求数据
     * - lostType 类型
     * - lostName 名称
     * - lostDescription 描述
     * - file 图片文件
     */
    boolean publish(String lostType, String lostName, String lostDescription, MultipartFile file);

    /**
     * 查询我的失物招领数据
     */
    List<LostVO> queryMy(Long userId);

    /**
     * 删除我的失物招领数据
     */
    boolean deleteMy(Long lostId);

    /**
     * 查询所有失物招领数据
     */
    List<LostListVO> queryAll();
}
