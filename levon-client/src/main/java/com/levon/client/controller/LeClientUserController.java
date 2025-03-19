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
     * @return ResponseResult 包含用户信息的响应结果
     */
    @SystemLog("获取用户信息")
    @GetMapping("/userInfo")
    public ResponseResult userInfo(){
        return ResponseResult.okResult(sysUserService.getUserInfo());
    }

    /**
     * 编辑用户信息
     * @param userInfoDTO ClientUserInfoUpdateValidationDTO 包含需要更新的用户信息的DTO
     * @return ResponseResult 操作成功的响应结果
     */
    @SystemLog("编辑用户信息")
    @PutMapping("/userInfo")
    public ResponseResult editUserInfo(@Validated @RequestBody ClientUserInfoUpdateValidationDTO userInfoDTO){
        sysUserService.updateUserInfo(userInfoDTO);
        return ResponseResult.okResult();
    }

    /**
     * 处理用户注册请求
     * @param userInfoCreateValidationDTO ClientUserInfoCreateValidationDTO 包含用户注册信息的DTO
     * @return ResponseResult 注册成功的响应结果
     */
    @SystemLog("处理用户注册请求")
    @PostMapping("/register")
    public ResponseResult register(@Validated @RequestBody ClientUserInfoCreateValidationDTO userInfoCreateValidationDTO){
        sysUserService.register(userInfoCreateValidationDTO);
        return ResponseResult.okResult();
    }

}
