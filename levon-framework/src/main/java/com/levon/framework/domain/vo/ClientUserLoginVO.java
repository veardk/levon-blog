package com.levon.framework.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUserLoginVO {

    private String token;
    private UserInfoVO userInfo;
}