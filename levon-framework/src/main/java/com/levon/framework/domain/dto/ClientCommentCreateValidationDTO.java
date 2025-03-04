package com.levon.framework.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 评论验证DTO
 */
@Data
public class ClientCommentCreateValidationDTO implements Serializable {

    /**
     * 文章ID，不能为空
     */
    @NotNull(message = "Article ID cannot be null")
    private Long articleId;

    /**
     * 类型，0为文章评论，1为友链评论
     */
    @NotNull(message = "Comment type cannot be null")
    @Pattern(regexp = "[01]", message = "Type must be 0 or 1")
    private String type;

    /**
     * 根评论ID
     */
    private Long rootId;

    /**
     * 被回复的评论ID
     */
    private Long toCommentId;

    /**
     * 被回复的评论用户ID
     */
    private Long toCommentUserId;

    /**
     * 评论内容，不能为空，长度限制
     */
    @NotNull(message = "Content cannot be null")
    @Size(min = 1, max = 500, message = "Content must be between 1 and 500 characters")
    private String content;

    private static final long serialVersionUID = 1L;
}