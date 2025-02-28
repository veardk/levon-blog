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
import com.levon.framework.service.LeBlogLoginService;
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
public class LeBlogLoginServiceImpl implements LeBlogLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;



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


}




