package com.levon.framework.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminTagListVO implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 标签名
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}