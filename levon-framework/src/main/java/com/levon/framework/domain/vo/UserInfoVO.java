package com.levon.framework.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class UserInfoVO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户性别（0男，1女，2未知）
     */
    private String sex;

    /**
     * 邮箱
     */
    private String email;

    private static final long serialVersionUID = 134235535L;

}
