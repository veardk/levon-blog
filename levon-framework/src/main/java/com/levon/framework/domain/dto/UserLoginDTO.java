package com.levon.framework.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {

    @NotNull(message = "用户名不能为空")
    private String userName;

    @NotNull(message = "密码不能为空")
    private String password;

    @Serial
    private static final long serialVersionUID = 1L;
}
