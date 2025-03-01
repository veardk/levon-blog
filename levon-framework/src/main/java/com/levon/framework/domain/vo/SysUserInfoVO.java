package com.levon.framework.domain.vo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import lombok.Data;

@Data
public class SysUserInfoVO implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户性别（0男，1女，2未知）
     */
    private String sex;

    /**
     * 头像
     */
    private String avatar;

    @TableField(exist = false)
    private static final long serialVersionUID = 134235535L;
}