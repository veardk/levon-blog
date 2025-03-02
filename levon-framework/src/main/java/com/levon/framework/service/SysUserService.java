package com.levon.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.dto.ClientUserInfoCreateValidationDTO;
import com.levon.framework.domain.dto.ClientUserInfoUpdateValidationDTO;
import com.levon.framework.domain.entry.SysUser;
import com.levon.framework.domain.vo.UserInfoVO;

public interface SysUserService extends IService<SysUser> {
    UserInfoVO getUserInfo();

    /**
     * 编辑用户信息
     * @param userInfoDTO 用户DTO
     * @return
     */
    void updateUserInfo(ClientUserInfoUpdateValidationDTO userInfoDTO);

    /**
     * 处理用户注册请求
     * 该方法接收用户信息DTO进行用户注册，并返回操作结果。
     *
     * @param userInfoCreateValidationDTO 用户注册DTO，包含用户的必要注册信息，如用户名、密码、邮箱等
     * @return 返回注册成功的响应结果
     */
    void register(ClientUserInfoCreateValidationDTO userInfoCreateValidationDTO);
}
