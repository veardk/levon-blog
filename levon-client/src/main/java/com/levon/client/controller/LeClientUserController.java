package com.levon.client.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.ClientUserInfoCreateValidationDTO;
import com.levon.framework.domain.dto.ClientUserInfoUpdateValidationDTO;
import com.levon.framework.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class LeClientUserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取用户信息
     * @return
     */
    @SystemLog("获取用户信息")
    @GetMapping("/userInfo")
    public ResponseResult userInfo(){
        return ResponseResult.okResult(sysUserService.getUserInfo());
    }

    /**
     * 编辑用户信息
     * @param userInfoDTO 用户DTO
     * @return
     */
    @SystemLog("编辑用户信息")
    @PutMapping("/userInfo")
    public ResponseResult editUserInfo(@Validated @RequestBody ClientUserInfoUpdateValidationDTO userInfoDTO){
        sysUserService.updateUserInfo(userInfoDTO);
        return ResponseResult.okResult();
    }

    /**
     * 处理用户注册请求
     * 该方法接收用户信息DTO进行用户注册，并返回操作结果。
     *
     * @param userInfoCreateValidationDTO 用户注册DTO，包含用户的必要注册信息，如用户名、密码、邮箱等
     * @return 返回注册成功的响应结果
     */
    @SystemLog("处理用户注册请求")
    @PostMapping("/register")
    public ResponseResult register(@Validated @RequestBody ClientUserInfoCreateValidationDTO userInfoCreateValidationDTO){
        sysUserService.register(userInfoCreateValidationDTO);
        return ResponseResult.okResult();
    }

}
