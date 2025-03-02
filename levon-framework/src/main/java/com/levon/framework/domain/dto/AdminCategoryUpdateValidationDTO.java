package com.levon.framework.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCategoryUpdateValidationDTO implements Serializable {
    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 分类名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态0:正常,1禁用
     */
    @Pattern(regexp = "0|1", message = "状态必须为 0 或 1")
    private String status;


    private static final long serialVersionUID = 1L;
}
