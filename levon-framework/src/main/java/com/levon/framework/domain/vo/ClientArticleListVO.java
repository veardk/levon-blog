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
public class ClientArticleListVO implements Serializable {

    /**
     * 文章id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 分类名称
     */
    private String categoryName;

    // TODO VO放回插入标签组
    private List<String> tagName;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 访问量
     */
    private Long viewCount;

    /**
     *
     */
    private Date createTime;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
