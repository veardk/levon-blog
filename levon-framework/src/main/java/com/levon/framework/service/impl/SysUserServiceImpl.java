package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.common.util.JwtUtil;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.common.util.SecurityUtils;
import com.levon.framework.domain.dto.UserInfoCreateValidationDTO;
import com.levon.framework.domain.dto.UserInfoUpdateValidationDTO;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.domain.entry.LoginUser;
import com.levon.framework.domain.entry.SysUser;
import com.levon.framework.domain.vo.LeBlogUserLoginVO;
import com.levon.framework.domain.vo.SysUserInfoVo;
import com.levon.framework.domain.vo.UserInfoVO;
import com.levon.framework.service.SysUserService;
import com.levon.framework.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author leivik
 * @description 针对表【sys_user(用户表)】的数据库操作Service实现
 * @createDate 2025-02-22 09:53:58
 */

//LeBlogLoginServiceImpl
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 用户登陆
     *
     * @param user
     * @return
     */
    @Override
    public LeBlogUserLoginVO login(UserLoginDTO user) {
        // 获取用户输入的登陆json，根据数据到本地数据库验证及获取 用户信息和权限信息 (UserDetailServiceImpl)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());

        // 本地数据库获得的信息封装成Authentication(用户信息、用户权限信息...)
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        //判断是否认证通过，如果authenticate为null，说明账户或者密码错了没有查到
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }


        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();

        //获取userid 用userid生成token
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.generateToken(userId);

        //把用户信息存入redis,  (blog_login:1 : loginUser)
        redisCache.setCacheObject("blog_login:" + userId, loginUser);

        //把token和userinfo封装 返回
        //把User转换成UserInfoVo
        UserInfoVO userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVO.class);

        return new LeBlogUserLoginVO(jwt, userInfoVo);
    }

    /**
     * 用户退出
     */
    @Override
    public void logout() {
        //获取token 解析获取userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();

        //删除redis中的用户信息
        redisCache.deleteObject("blog_login:" + userId);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public SysUserInfoVo getUserInfo() {

        SysUser sysUser = sysUserMapper.selectById(SecurityUtils.getUserId());

        if (Objects.isNull(sysUser)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "账户异常");
        }

        return BeanCopyUtils.copyBean(sysUser, SysUserInfoVo.class);
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




