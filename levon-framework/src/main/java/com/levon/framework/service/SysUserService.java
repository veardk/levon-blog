package com.levon.framework.service;

import com.levon.framework.domain.dto.UserInfoCreateValidationDTO;
import com.levon.framework.domain.dto.UserInfoUpdateValidationDTO;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.domain.entry.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.LeBlogUserLoginVO;
import com.levon.framework.domain.vo.SysUserInfoVo;

/**
* @author leivik
* @description 针对表【sys_user(用户表)】的数据库操作Service
* @createDate 2025-02-22 09:53:58
*/

//LeBlogLoginService
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     * @param user
     * @return
     */
    LeBlogUserLoginVO login(UserLoginDTO user);

    /**
     * 用户退出
     */
    void logout();

    /**
     * 获取用户信息
     * @return
     */
    SysUserInfoVo getUserInfo();

    /**
     * 编辑用户信息
     * @param userInfoDTO 用户DTO
     * @return
     */
    void updateUserInfo(UserInfoUpdateValidationDTO userInfoDTO);

    /**
     * 处理用户注册请求
     * 该方法接收用户信息DTO进行用户注册，并返回操作结果。
     *
     * @param userInfoCreateValidationDTO 用户注册DTO，包含用户的必要注册信息，如用户名、密码、邮箱等
     * @return 返回注册成功的响应结果
     */
    void register(UserInfoCreateValidationDTO userInfoCreateValidationDTO);
}
