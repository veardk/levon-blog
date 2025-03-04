package com.levon.framework.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
public class AdminArticleUpdateValidationDTO implements Serializable {

    /**
     *
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 所属分类id
     */
    private Long categoryId;

    /**
     * 所属标签id
     */
    private List<Long> tags;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 是否置顶（0否，1是）
     */
    @Pattern(regexp = "0|1", message = "是否置顶状态必须为 0 或 1")
    private String isTop;

    /**
     * 状态（0已发布，1草稿）
     */
    @Pattern(regexp = "0|1", message = "状态必须为 0 或 1")
    private String status;

    /**
     * 是否允许评论 1是，0否
     */
    @Pattern(regexp = "0|1", message = "是否允许评论必须为 0 或 1")
    private String isComment;


    private static final long serialVersionUID = 1L;

}
