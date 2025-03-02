package com.levon.framework.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCategoryListVO implements Serializable {
    /**
     * id
     */
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
    private String status;


    private static final long serialVersionUID = 1L;
}
