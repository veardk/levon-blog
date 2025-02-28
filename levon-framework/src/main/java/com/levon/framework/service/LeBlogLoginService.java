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
public interface LeBlogLoginService {

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


}
