package com.levon.framework.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientLinkVO implements Serializable {

    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String logo;

    /**
     * 
     */
    private String description;

    /**
     * 网站地址
     */
    private String address;

    private static final long serialVersionUID = 1L;
}