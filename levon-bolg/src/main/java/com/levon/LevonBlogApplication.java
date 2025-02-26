package com.levon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.levon.framework.mapper")
@EnableScheduling
public class LevonBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(LevonBlogApplication.class, args);
    }

}