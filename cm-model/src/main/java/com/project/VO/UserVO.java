package com.project.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户手机号码
     */
    private String userPhone;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 0-女 1-男 2-暂无
     */
    private Integer userGender;

    /**
     * 用户生日
     */
    private String userBirthday;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户所在地
     */
    private String userLocation;

    /**
     * 用户家乡
     */
    private String userHometown;

    /**
     * 用户专业
     */
    private String userProfession;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户标签
     */
    private String userTags;

    /**
     * 是否是管理员
     */
    private Integer isAdmin;
}
