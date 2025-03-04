package com.levon.framework.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AdminTagUpdateValidationDTO implements Serializable {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String name;

    private String remark;

    private static final long serialVersionUID = 1L;
}
