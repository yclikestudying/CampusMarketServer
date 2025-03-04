package com.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.VO.express.ExpressListVO;
import com.project.domain.Express;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExpressMapper extends BaseMapper<Express> {
    // 查询所有的跑腿服务（排除自己的）
    List<ExpressListVO> queryAllExpress();
}
