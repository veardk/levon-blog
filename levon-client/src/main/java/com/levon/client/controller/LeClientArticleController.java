package com.levon.client.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.service.LeBlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class LeClientArticleController {

    @Autowired
    private LeBlogArticleService leBlogArticleService;

    /**
     * 获取热门文章列表
     *
     * @return
     */
    @SystemLog("获取热门文章列表")
    @GetMapping("/hotArticleList")
    public ResponseResult getHotArticle() {
        return ResponseResult.okResult(leBlogArticleService.hotArticleList());
    }

    /**
     * 文章列表
     */
    @SystemLog("文章列表")
    @GetMapping("/articleList")
    public ResponseResult articleList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Long categoryId) {
        return ResponseResult.okResult(leBlogArticleService.articleList(pageNum, pageSize, categoryId));

    }

    /**
     * 文章详细
     */
    @SystemLog("文章详细")
    @GetMapping("/{id}")
    public ResponseResult articleDetail(@PathVariable Long id){
        return ResponseResult.okResult(leBlogArticleService.articleDetail(id));
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        leBlogArticleService.updateViewCount(id);
        return ResponseResult.okResult();
    }
}
