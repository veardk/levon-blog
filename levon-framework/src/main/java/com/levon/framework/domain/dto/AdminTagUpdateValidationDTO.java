package com.levon.framework.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminTagUpdateValidationDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String name;

    private String remark;
}
