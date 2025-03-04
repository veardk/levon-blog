package com.levon.framework.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class ChangeLinkStatusValidationDTO implements Serializable {

    @NotNull(message = "ID不能为空")
    private Long id;

    @Pattern(regexp = "0|1|2", message = "是否置顶状态必须为 0 、1、2")
    private String status;

    private static final long serialVersionUID = 1L;
}
