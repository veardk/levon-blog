package com.levon.bolg.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.service.LeBlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeBlogLoginController {

    @Autowired
    private LeBlogLoginService leBlogLoginService;

    /**
     * 用户登陆
     * @param user
     * @return
     */
    @SystemLog("用户登陆")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserLoginDTO user){
        return ResponseResult.okResult(leBlogLoginService.login(user));
    }

    /**
     * 用户退出
     * @return
     */
    @SystemLog("用户退出")
    @PostMapping("/logout")
    public ResponseResult logout(){
        leBlogLoginService.logout();
        return ResponseResult.okResult();
    }

}
