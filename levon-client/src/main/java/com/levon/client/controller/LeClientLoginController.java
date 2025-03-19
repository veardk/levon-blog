package com.levon.client.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.service.LeBlogClientLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeClientLoginController {

    @Autowired
    private LeBlogClientLoginService leBlogLoginService;

    /**
     * 用户登陆
     * @param user UserLoginDTO 包含用户登录信息的DTO
     * @return ResponseResult 包含登录结果的响应结果
     */
    @SystemLog("用户登陆")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserLoginDTO user){
        return ResponseResult.okResult(leBlogLoginService.login(user));
    }

    /**
     * 用户退出
     * @return ResponseResult 操作成功的响应结果
     */
    @SystemLog("用户退出")
    @PostMapping("/logout")
    public ResponseResult logout(){
        leBlogLoginService.logout();
        return ResponseResult.okResult();
    }

}
