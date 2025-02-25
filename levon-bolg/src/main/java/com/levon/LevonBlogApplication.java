package com.levon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.levon.framework.mapper")
public class LevonBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(LevonBlogApplication.class, args);
    }

}