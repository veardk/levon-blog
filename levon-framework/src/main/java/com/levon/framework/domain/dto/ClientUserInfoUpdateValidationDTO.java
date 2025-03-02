package com.levon.framework.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;

@Data
@Accessors(chain = true)
public class ClientUserInfoUpdateValidationDTO {
    /**
     * 主键
     * @NotNull 用于确保 id 不能为空
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 昵称
     * @NotBlank 用于确保昵称不能为空
     * @Size 用于限制昵称长度在 1 到 50 之间
     */
    @Size(min = 1, max = 50, message = "昵称长度必须在 1 到 50 之间")
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别: 0.男 1.女
     * @Pattern 用于验证 sex 字段只能是 "0" 或 "1"
     */
    @Pattern(regexp = "0|1", message = "性别必须为 0 或 1")
    private String sex;

    /**
     * 邮箱
     * @Email 用于验证邮箱格式
     */
    @Email(message = "邮箱格式不正确")
    private String email;


}
