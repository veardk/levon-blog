package com.levon.framework.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminLinkVO implements Serializable {

    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * logo
     */
    private String logo;

    /**
     * 描述
     */
    private String description;

    /**
     * 网站地址
     */
    private String address;

    /**
     * 审核状态 (0代表审核通过，1代表审核未通过，2代表未审核)
     */
    private String status;

    private static final long serialVersionUID = 1L;
}