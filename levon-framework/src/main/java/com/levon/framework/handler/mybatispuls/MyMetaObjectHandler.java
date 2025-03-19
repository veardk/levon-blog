package com.levon.framework.handler.mybatispuls;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.levon.framework.common.util.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    private static final Logger log = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        // BUG: 当使用非登陆接口，且接口会对数据库进行创建和更新操作，这时，使用自动填充创建、更新时间、创建、更新人字段时，获取userId会报错！

        Long userId = getUserId();
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy", userId, metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = getUserId();
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", userId, metaObject);
    }

    private static Long getUserId() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            log.error("无法获取当前用户ID，使用默认值 -1", e);
            return -1L;
        }
    }
}