package com.levon.framework.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class AdminLinkCreateValidationDTO implements Serializable {

    /**
     * 名称
     */
    @NotNull(message = "名称不能为空")
    private String name;

    /**
     * logo
     */
    private String logo;

    /**
     * 描述
     */
    private String description;

    /**
     * 网站地址
     */
    @NotNull(message = "网站地址不能为空")
    private String address;

    /**
     * 审核状态 (0代表审核通过，1代表审核未通过，2代表未审核)
     */
    @Pattern(regexp = "0|1|2", message = "是否置顶状态必须为 0 、1、2")
    private String status;

    private static final long serialVersionUID = 1L;
}
