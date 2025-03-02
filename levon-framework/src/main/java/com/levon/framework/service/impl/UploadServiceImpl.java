package com.levon.framework.service.impl;

import com.google.gson.Gson;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
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

import java.io.InputStream;

@Service
public class UploadServiceImpl implements UploadService {

    private String accessKey = "R5_mkdDbG1o8OqC-tnjYSefdHtvkHNYVUWZy2YSz";
    private String secretKey = "MVM_yH-8QvHy09NbvSd-8CpncEx1EX83laDKqsps";
    private String bucket = "levon-blog";
    private String fileBucketUrl = "http://ss6so06zb.hb-bkt.clouddn.com/"; // 外链域名

//    private String accessKey;
//    private String secretKey;
//    private String bucket;
//    private String bucketUrl      // 外链域名

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * 上传图片
     * @param img 图片文件
     * @return String 外链访问的 URL
     */
    @Override
    public String uploadImg(MultipartFile img) {

        // 判断文件类型只允许上传 png,jpg格式
        String originalFilename = img.getOriginalFilename();
        if (!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")) {
            throw new SystemException(AppHttpCodeEnum.UPLOAD_FILE_TYPE_ERROR);
        }

        // 文件路径名格式化
        String imgPath = PathUtils.generateFilePath(originalFilename);

        String url = uploadOss(img, imgPath);

        return url;
    }

    /**
     * OSS上传
     * @param img 图片文件
     * @param imgKey 图片在OSS中的路径和命名
     * @retun String 外链访问的 URLr
     */
    private String uploadOss(MultipartFile img, String imgKey) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传

        // 命名文件名
        String key = imgKey;

        try {
            InputStream inputStream = img.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream, key, upToken, null, null);

                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

                return fileBucketUrl + putRet.key;

            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore

        }

        return "Error";
    }

    // TODO 优化上传图片
//    private static final Logger logger = Logger.getLogger(UploadServiceImpl.class.getName());
//    private static final String[] ALLOWED_TYPES = {".png", ".jpg"};
//    @Value("${qiniu.accessKey}")
//    private String accessKey;
//    @Value("${qiniu.secretKey}")
//    private String secretKey;
//    @Value("${qiniu.bucket}")
//    private String bucket;
//    @Value("${qiniu.fileBucketUrl}")
//    private String fileBucketUrl;
//    @Override
//    public String uploadImg(MultipartFile img) {
//        String originalFilename = img.getOriginalFilename();
//        if (!isValidFileType(originalFilename)) {
//            throw new SystemException(AppHttpCodeEnum.UPLOAD_FILE_TYPE_ERROR);
//        }
//        String imgPath = PathUtils.generateFilePath(originalFilename);
//        return uploadOss(img, imgPath);
//    }
//    private boolean isValidFileType(String filename) {
//        for (String type : ALLOWED_TYPES) {
//            if (filename.endsWith(type)) {
//                return true;
//            }
//        }
//        return false;
//    }
//    private String uploadOss(MultipartFile img, String imgKey) {
//        Configuration cfg = new Configuration(Region.autoRegion());
//        UploadManager uploadManager = new UploadManager(cfg);
//        try (InputStream inputStream = img.getInputStream()) {
//            Auth auth = Auth.create(accessKey, secretKey);
//            String upToken = auth.uploadToken(bucket);
//            Response response = uploadManager.put(inputStream, imgKey, upToken, null, null);
//            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            return fileBucketUrl + putRet.key;
//        } catch (QiniuException ex) {
//            logger.severe("上传失败：" + ex.response.toString());
//            throw new SystemException(AppHttpCodeEnum.UPLOAD_FAILED);
//        } catch (Exception ex) {
//            logger.severe("未知错误：" + ex.getMessage());
//            throw new SystemException(AppHttpCodeEnum.UNKNOWN_ERROR);
//        }
//    }
}
