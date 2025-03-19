package com.levon.controller;

import com.levon.LevonClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@SpringBootTest(classes = LevonClientApplication.class)
@Component
@Slf4j
public class OssTest {
    @Test
    public void passwordTest(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("1234");
        System.out.println("encode = " + encode);

    }

    @Test
    public void testLog(){
        log.info("输出: {}", 123);
    }

}
