package com.levon.client.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.service.LeBlogCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class LeClientCategoryController {


    /**
     * dev  coding
     */

    @Autowired
    private LeBlogCategoryService leBlogCategoryService;

    /**
     * 获取文章分类列表
     * @return
     */
    @SystemLog("获取文章分类列表")
    @GetMapping("/getCategoryList")
    public ResponseResult<Object> getCategoryList(){
        return ResponseResult.okResult(leBlogCategoryService.getCateGoryList());
    }
}
