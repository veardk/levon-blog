package com.levon.framework.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeBlogCategoryVO {

    /**
     * 分类id
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;
}
