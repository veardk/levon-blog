package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.common.util.SecurityUtils;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.service.LeBlogAdminLoginService;
import com.levon.framework.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeAdminLoginController {

    @Autowired
    private LeBlogAdminLoginService leBlogAdminLoginService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 管理员用户登陆
     *
     * @param user 用户登陆信息DTO
     * @return ResponseResult
     */
    @SystemLog("管理员用户登陆")
    @PostMapping("/user/login")
    public ResponseResult login(@Validated @RequestBody UserLoginDTO user) {
        return ResponseResult.okResult(leBlogAdminLoginService.login(user));
    }

    /**
     * 管理员用户退出
     * @return
     */
    @SystemLog("管理员用户退出")
    @PostMapping("/user/logout")
    public ResponseResult logout(){
        leBlogAdminLoginService.logout();
        return ResponseResult.okResult();
    }

    /**
     * 获取管理员权限信息
     *
     * @return
     */
    @GetMapping("/getInfo")
    @SystemLog("获取管理员权限信息")
    public ResponseResult getInfo() {
        if (SecurityUtils.isAdmin()) {
            return ResponseResult.okResult(sysMenuService.getAdminInfo());
        } else {
            return ResponseResult.okResult(sysMenuService.getOtherAdminInfo());
        }
    }

    /**
     * 获取当前用户的菜单路由信息
     * <p>
     * 该方法根据当前用户的权限，从数据库中查询对应的菜单信息，并将其组装为树形结构的路由数据返回给前端，用于动态生成菜单。
     * 如果是超级管理员获取所有菜单路由，否则获取相应的信息
     */

    @GetMapping("/getRouters")
    @SystemLog("获取当前用户的菜单路由信息")
    public ResponseResult getRouters() {

        if (SecurityUtils.isAdmin()) {
            return ResponseResult.okResult(sysMenuService.selectAllMenuRouter());
        } else {
            return ResponseResult.okResult(sysMenuService.selectMenuRouterTreeByUserId(SecurityUtils.getUserId()));
        }
    }

}
