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
     * @return ResponseResult 包含热门文章列表的响应结果
     */
    @SystemLog("获取热门文章列表")
    @GetMapping("/hotArticleList")
    public ResponseResult getHotArticle() {
        return ResponseResult.okResult(leBlogArticleService.hotArticleList());
    }

    /**
     * 文章列表
     * @param pageNum 当前页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @param categoryId 分类ID，可选
     * @return ResponseResult 包含文章列表的响应结果
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
     * @param id 文章ID
     * @return ResponseResult 包含文章详细信息的响应结果
     */
    @SystemLog("文章详细")
    @GetMapping("/{id}")
    public ResponseResult articleDetail(@PathVariable Long id){
        return ResponseResult.okResult(leBlogArticleService.articleDetail(id));
    }

    /**
     * 更新文章浏览量
     * @param id 文章ID
     * @return ResponseResult 操作成功的响应结果
     */
    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        leBlogArticleService.updateViewCount(id);
        return ResponseResult.okResult();
    }
}
