package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.common.util.SecurityUtils;
import com.levon.framework.domain.dto.UserInfoCreateValidationDTO;
import com.levon.framework.domain.dto.UserInfoUpdateValidationDTO;
import com.levon.framework.domain.entry.SysUser;
import com.levon.framework.domain.vo.SysUserInfoVO;
import com.levon.framework.mapper.SysUserMapper;
import com.levon.framework.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public SysUserInfoVO getUserInfo() {

        SysUser sysUser = sysUserMapper.selectById(SecurityUtils.getUserId());

        if (Objects.isNull(sysUser)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "账户异常");
        }

        return BeanCopyUtils.copyBean(sysUser, SysUserInfoVO.class);
    }

    /**
     * 编辑用户信息
     *
     * @param userInfoDTO
     */
    @Override
    public void updateUserInfo(UserInfoUpdateValidationDTO userInfoDTO) {
        SysUser sysUser = BeanCopyUtils.copyBean(userInfoDTO, SysUser.class);
        Long userId = SecurityUtils.getUserId();
        sysUser.setUpdateBy(userId);
        if (!updateById(sysUser)) {
            throw new SystemException(AppHttpCodeEnum.USER_INFO_EDIT_ERROR);
        }
    }

    /**
     * 处理用户注册请求
     * 该方法接收用户信息DTO进行用户注册，并返回操作结果。
     *
     * @param userInfoCreateValidationDTO 用户注册DTO，包含用户的必要注册信息，如用户名、密码、邮箱等
     * @return 返回注册成功的响应结果
     */
    @Override
    public void register(UserInfoCreateValidationDTO userInfoCreateValidationDTO) {
        checkUserInfoExist(userInfoCreateValidationDTO);

        SysUser sysUser = BeanCopyUtils.copyBean(userInfoCreateValidationDTO, SysUser.class);

        // 用户密码加密
        String encodePassword = new BCryptPasswordEncoder().encode(sysUser.getPassword());
        sysUser.setPassword(encodePassword);

        if (!save(sysUser)) {
            throw new SystemException(AppHttpCodeEnum.USER_REGISTER_ERROR);
        }

    }

    /**
     * 检查用户信息是否已存在
     */
    private void checkUserInfoExist(UserInfoCreateValidationDTO userInfo) {
        boolean userNameExists = sysUserMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, userInfo.getUserName()));

        if (userNameExists) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        boolean nickExists = sysUserMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getNickName, userInfo.getNickName()));

        if (nickExists) {
            throw new SystemException(AppHttpCodeEnum.USER_NICK_NAME_EXISTS);
        }

        boolean emailExists = sysUserMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, userInfo.getEmail()));

        if (emailExists) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
    }

}
