package com.levon.framework.service.impl;

import com.levon.framework.common.util.JwtUtil;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.domain.entry.LoginUser;
import com.levon.framework.domain.vo.LeBlogAdminUserInfoVo;
import com.levon.framework.service.LeBlogAdminLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LeBlogAdminLoginServiceImpl implements LeBlogAdminLoginService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;



    /**
     * 管理员用户登陆
     *
     * @param user
     * @return
     */
    @Override
    public Map<String, String> login(UserLoginDTO user) {
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

        //把用户信息存入redis,  (blog_admin_login:1 : loginUser)
        redisCache.setCacheObject("blog_admin_login:" + userId, loginUser);

        //把token封装 返回
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);

        return map;
    }

    /**
     * 管理员用户退出
     */
    @Override
    public void logout() {
        //获取token 解析获取userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();

        //删除redis中的用户信息
        redisCache.deleteObject("blog_admin_login:" + userId);
    }

    @Override
    public LeBlogAdminUserInfoVo getInfo() {


        return null;
    }

}
