package com.levon.bolg.controller;

import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传图片
     * @param img 图片文件
     * @return
     */
    @PostMapping("/upload")
    public ResponseResult upload(MultipartFile img){
        return ResponseResult.okResult(uploadService.uploadImg(img));
    }
}
