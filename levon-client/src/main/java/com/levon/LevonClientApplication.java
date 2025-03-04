package com.levon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "com.levon")
@MapperScan("com.levon.framework.mapper")
@EnableScheduling
@EnableSwagger2
public class LevonClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(LevonClientApplication.class, args);
    }

}