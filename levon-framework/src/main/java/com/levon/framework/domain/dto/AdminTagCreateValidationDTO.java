package com.levon.framework.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AdminTagCreateValidationDTO implements Serializable {

    @NotNull(message = "名称不能为空")
    private String name;

    private String remark;

    private static final long serialVersionUID = 1L;
}
