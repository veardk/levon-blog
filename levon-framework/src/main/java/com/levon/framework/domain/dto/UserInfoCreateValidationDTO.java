package com.levon.framework.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;

@Data
@Accessors(chain = true)
public class UserInfoCreateValidationDTO {

    /**
     * 账号名
     * 要求：
     * 1. 不能为空
     * 2. 长度在4-20之间
     * 3. 只能包含字母、数字、下划线
     */
    @NotBlank(message = "账号名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号名必须是4-20位字母、数字或下划线")
    private String userName;

    /**
     * 密码
     * 要求：
     * 1. 不能为空
     * 2. 长度在8-30之间
     * 3. 必须包含大小写字母、数字和特殊字符
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
            message = "密码必须包含大小写字母、数字，长度8-20位")
    private String password;

    /**
     * 昵称
     * 要求：
     * 1. 不能为空
     * 2. 长度在1-50之间
     * 3. 支持中文、字母、数字
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 50, message = "昵称长度必须在1到50之间")
    @Pattern(regexp = "^[a-zA-Z0-9\u4e00-\u9fa5]+$", message = "昵称只能包含中文、字母和数字")
    private String nickName;

    /**
     * 邮箱
     * 要求：
     * 1. 不能为空
     * 2. 符合标准邮箱格式
     * 3. 长度限制
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

}
