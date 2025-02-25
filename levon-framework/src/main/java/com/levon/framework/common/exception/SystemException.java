package com.levon.framework.common.exception;

import com.levon.framework.common.enums.AppHttpCodeEnum;

/**
 * 请求参数自定义异常处理 : 对请求参数的合法性进行判断，所以要有提示
 */
public class SystemException extends RuntimeException{

    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

    // 自定义msg
    public SystemException(AppHttpCodeEnum httpCodeEnum, String msg) {
        super(msg);
        this.code = httpCodeEnum.getCode();
        this.msg = msg;
    }
    
}