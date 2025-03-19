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

    @Autowired
    private LeBlogCategoryService leBlogCategoryService;

    /**
     * 获取文章分类列表
     * @return ResponseResult 包含文章分类列表的响应结果
     */
    @SystemLog("获取文章分类列表")
    @GetMapping("/getCategoryList")
    public ResponseResult getCategoryList(){
        return ResponseResult.okResult(leBlogCategoryService.getCateGoryList());
    }
}
