package com.levon.framework.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminTagCreateValidationDTO {

    // FIXME 为什么没有拦截
    @NotNull(message = "名称不能为空")
    private String name;

    private String remark;

}
