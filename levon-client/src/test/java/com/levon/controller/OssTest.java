package com.levon.controller;

import com.google.gson.Gson;

import com.levon.LevonBlogApplication;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest(classes = LevonBlogApplication.class)
@Component
@ConfigurationProperties(prefix = "oss")
public class OssTest {
    private String accessKey = "R5_mkdDbG1o8OqC-tnjYSefdHtvkHNYVUWZy2YSz";
    private String secretKey = "MVM_yH-8QvHy09NbvSd-8CpncEx1EX83laDKqsps";
    private String bucket = "levon-blog";

//    private String accessKey;
//    private String secretKey;
//    private String bucket;


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

    @Test
    public void ossTest(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
//        String accessKey = "your access key";
//        String secretKey = "your secret key";
//        String bucket = "sg-blog";

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;

        try {
//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);


            InputStream inputStream = new FileInputStream("/Users/leivik/Desktop/50音.jpg");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
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
    }

    @Test
    public void oss() {
        System.out.println("AccessKey: " + accessKey);
        System.out.println("SecretKey: " + secretKey);
        System.out.println("Bucket: " + bucket);

        // 打印出当前系统的属性
        System.out.println("Property accessKey: " + System.getProperty("oss.accessKey"));
        System.out.println("Property secretKey: " + System.getProperty("oss.secretKey"));
        System.out.println("Property bucket: " + System.getProperty("oss.bucket"));
    }

    @Test
    public void passwordTest(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("1234");
        System.out.println("encode = " + encode);

    }

}
