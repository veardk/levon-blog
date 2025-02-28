package com.levon.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.levon")
@MapperScan("com.levon.framework.mapper")
public class LevonAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(LevonAdminApplication.class, args);
    }
}
