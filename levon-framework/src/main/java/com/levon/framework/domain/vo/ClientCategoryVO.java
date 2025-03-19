package com.levon.framework.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientCategoryVO {

    /**
     * 分类id
     */
    private Long id;
    // TODO 为什么id一会是整形，一会儿是字符型（返回的JSON）

    /**
     * 分类名称
     */
    private String name;
}
