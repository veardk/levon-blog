package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.service.LeBlogAdminLoginService;
import com.levon.framework.service.LeBlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeBlogAdminLoginController {

    @Autowired
    private LeBlogAdminLoginService leBlogAdminLoginService;

    /**
     * 管理员用户登陆
     * @param user
     * @return
     */
    @SystemLog("管理员用户登陆")
    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody UserLoginDTO user){
        return ResponseResult.okResult(leBlogAdminLoginService.login(user));
    }

    /**
     * 管理员用户退出
     * @return
     */
//    @SystemLog("管理员用户退出")
//    @PostMapping("/user/logout")
//    public ResponseResult logout(){
//        leBlogAdminLoginService.logout();
//        return ResponseResult.okResult();
//    }

    @GetMapping("getInfo")
    public ResponseResult getInfo(){
        return ResponseResult.okResult(leBlogAdminLoginService.getInfo());
    }





}
