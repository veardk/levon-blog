package com.levon.bolg.controller;

import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.UserLoginDTO;
import com.levon.framework.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeBlogLoginController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserLoginDTO user){
        return ResponseResult.okResult(sysUserService.login(user));
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        sysUserService.logout();
        return ResponseResult.okResult();
    }



}
