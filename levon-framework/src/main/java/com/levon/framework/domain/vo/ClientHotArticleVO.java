package com.levon.framework.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientHotArticleVO {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 访问量
     */
    private Long viewCount;
}
