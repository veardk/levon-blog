package com.levon.framework.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminTagVO implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 标签名
     */
    private String name;

    private static final long serialVersionUID = 1L;
}