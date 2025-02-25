package com.levon.framework.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface UploadService {


    /**
     * 上传图片
     * @param img 图片文件
     * @return String 外链访问的url
     */
    String uploadImg(MultipartFile img);
}
