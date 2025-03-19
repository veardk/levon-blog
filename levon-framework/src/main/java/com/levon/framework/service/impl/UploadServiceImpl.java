package com.levon.framework.service.impl;

import com.google.gson.Gson;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.OSSProperties;
import com.levon.framework.common.util.PathUtils;
import com.levon.framework.service.UploadService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.logging.Logger;

@Service
public class UploadServiceImpl implements UploadService {

    @Resource
    private OSSProperties ossProperties;

    private static final Logger logger = Logger.getLogger(UploadServiceImpl.class.getName());
    private static final String[] ALLOWED_TYPES = {".png", ".jpg", ".jpeg"};

    /**
     * 上传图片
     *
     * @param img 图片文件
     * @return String 外链访问的 URL
     */
    @Override
    public String uploadImg(MultipartFile img) {

        String originalFilename = img.getOriginalFilename();

        if(originalFilename == null){
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        // 判断文件类型
        if (!isValidFileType(originalFilename)) {
            throw new SystemException(AppHttpCodeEnum.UPLOAD_FILE_TYPE_ERROR);
        }

        // 文件路径名格式化 -> eg:"2025/03/03/e31ad5d96ea04b36933396ebf505a512.jpg"
        String imgPath = PathUtils.generateFilePath(originalFilename);
        String url = uploadOss(img, imgPath);

        return url;
    }

    /**
     * OSS上传
     *
     * @param img    图片文件
     * @param imgKey 图片在OSS中的路径和命名
     * @return String 外链访问的 URL
     */
    private String uploadOss(MultipartFile img, String imgKey) {
        if (ossProperties == null) {
            throw new IllegalStateException("OSSProperties is not initialized");
        }

        // 构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        // 图片在OSS中的路径和命名,作为key
        String key = imgKey;
        try (InputStream inputStream = img.getInputStream()) {
            Auth auth = Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey());
            String upToken = auth.uploadToken(ossProperties.getBucket());
            Response response = uploadManager.put(inputStream, key, upToken, null, null);

            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            // OSS外链域名 与 key拼接 eg："http://s9so06zb.hb-bkt.clouddn.com/" + "2025/03/03/e31ad5d96ea04b36933396ebf505a512.jpg"
            return ossProperties.getFileBucketUrl() + putRet.key;
        } catch (QiniuException ex) {
            logger.severe("图片上传失败：" + ex.response.toString());
            throw new SystemException(AppHttpCodeEnum.UPLOAD_FAILED);
        } catch (Exception ex) {
            logger.severe("未知错误：" + ex.getMessage());
            throw new SystemException(AppHttpCodeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 判断文件类型是否合法
     *
     * @param filename 文件名
     * @return 返回true表示合法，false表示不合法
     */
    private boolean isValidFileType(String filename) {
        for (String type : ALLOWED_TYPES) {
            if (filename.endsWith(type)) {
                return true;
            }
        }
        return false;
    }

}
