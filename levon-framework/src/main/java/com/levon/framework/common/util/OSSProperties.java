package com.levon.framework.common.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oss") // 这里指定前缀
public class OSSProperties {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String fileBucketUrl;

    // Getters and Setters

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileBucketUrl() {
        return fileBucketUrl;
    }

    public void setFileBucketUrl(String fileBucketUrl) {
        this.fileBucketUrl = fileBucketUrl;
    }
}
