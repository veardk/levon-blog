package com.levon.framework.service;

import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.domain.vo.LeBlogAdminUserInfoVo;

import java.util.Map;

public interface LeBlogAdminLoginService {

    /**
     * 管理员用户登录
     *
     * @param user
     * @return
     */
    Map<String, String> login(UserLoginDTO user);

    /**
     * 管理员用户退出
     */
    void logout();

    /**
     * 获取管理员权限信息
     *
     * @return
     */
    LeBlogAdminUserInfoVo getInfo();
}
