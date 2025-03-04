package com.levon.framework.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
public class AdminArticleDetailVO implements Serializable {

    /**
     * 文章id
     */
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
     * 分类id
     */
    private Long categoryId;

    /**
     * 状态（0已发布，1草稿）
     */
    private String status;

    /**
     * 是否置顶（0否，1是）
     */
    private String isTop;

    /**
     * 是否允许评论 1是，0否
     */
    private String isComment;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签
     */
    private List<Long> tags;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 访问量
     */
    private Long viewCount;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     *
     */
    private Date createTime;


    /**
     * 更新人
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;

}
