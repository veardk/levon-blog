package com.levon.framework.handler.exception;

import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 异常捕捉处理器： 异常捕捉 -> 通过捕捉到的后端异常报错-->返回给前端响应
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 捕捉自定义异常SystemException
    @ExceptionHandler(SystemException.class)
    public ResponseResult systemExceptionHandler(SystemException e) {
        //打印异常信息
        log.error("出现了异常！{}", e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(e.getCode(), e.getMsg());
    }

    // 捕捉其他未处理的异常
    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception e) {
        log.error("系统异常：{}", e);
        // 处理一些无法预料的系统异常
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "系统错误，请联系管理员").withDebugMsg(e.getMessage());
    }

    // 捕捉数据验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult validationExceptionHandler(MethodArgumentNotValidException e) {
        log.error("数据验证失败：{}", e);
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append("; "));
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "数据验证失败: " + errorMsg.toString());
    }

}