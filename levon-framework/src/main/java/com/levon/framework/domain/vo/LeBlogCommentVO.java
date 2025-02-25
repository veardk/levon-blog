package com.levon.framework.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class LeBlogCommentVO implements Serializable {
    /**
     * 评论id
     */
    private Long id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 根评论id
     */
    private Long rootId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 所回复的目标评论的userid
     */
    private Long toCommentUserId;


    /**
     * 所回复的目标评论的username
     */
    private String toCommentUsername;

    /**
     * 回复目标评论id
     */
    private Long toCommentId;

    /**
     * 子评论
     */
    private List<LeBlogCommentVO> children;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String username;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}