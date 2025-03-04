package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.DTO.ExpressDTO;
import com.project.VO.express.ExpressListVO;
import com.project.VO.express.ExpressVO;
import com.project.domain.Express;

import java.util.List;

public interface ExpressService extends IService<Express> {
    /**
     * 跑腿内容上传
     * 请求数据
     * - content 文本内容
     * - price 价格
     */
    boolean upload(ExpressDTO expressDTO);

    /**
     * 查询自己发布的跑腿服务
     * 请求数据
     * - userId 用户id
     */
    List<ExpressVO> queryMyExpress(Long userId);

    /**
     * 查询所有的跑腿服务（排除自己的）
     */
    List<ExpressListVO> queryAllExpress();

    /**
     * 删除自己的跑腿服务
     * 请求数据
     * - expressId 跑腿服务id
     */
    boolean deleteExpress(Long expressId);
}
