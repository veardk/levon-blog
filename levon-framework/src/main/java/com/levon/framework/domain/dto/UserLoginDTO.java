package com.levon.framework.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserLoginDTO {

    @NotNull(message = "用户名不能为空")
    private String userName;

    @NotNull(message = "密码不能为空")
    private String password;
}
