package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class LeAdminUploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传图片
     *
     * @param img 图片文件
     * @return url地址
     */
    @SystemLog("上传图片")
    @PostMapping("/upload")
    public ResponseResult upload(MultipartFile img) {
        return ResponseResult.okResult(uploadService.uploadImg(img));
    }
}
