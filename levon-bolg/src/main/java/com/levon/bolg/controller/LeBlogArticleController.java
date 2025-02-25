package com.levon.bolg.controller;

import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.service.LeBlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class LeBlogArticleController {

    @Autowired
    private LeBlogArticleService leBlogArticleService;

    /**
     * 获取热门文章列表
     *
     * @return
     */
    @GetMapping("/hotArticleList")
    public ResponseResult getHotArticle() {
        return ResponseResult.okResult(leBlogArticleService.hotArticleList());
    }

    /**
     * 文章列表
     */
    @GetMapping("/articleList")
    public ResponseResult articleList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Long categoryId) {
        return ResponseResult.okResult(leBlogArticleService.articleList(pageNum, pageSize, categoryId));

    }

    /**
     *  IPage-XML 分页方式测试：文章列表
     */
    @GetMapping("/test1")
    public ResponseResult test1(){
        PageVO pageVo = leBlogArticleService.articleListByXml(1, 10, null);
        return ResponseResult.okResult(pageVo);
    }

    /**
     *  Page-XML 分页方式测试：文章列表
     */
    @GetMapping("/test2")
    public ResponseResult test2(){
        PageVO pageVo = leBlogArticleService.articleListWithPage(1, 10, 1L);
        return ResponseResult.okResult(pageVo);
    }

    /**
     *  List-XML 分页方式测试：文章列表
     */
    @GetMapping("/test3")
    public ResponseResult test3(){
        PageVO pageVo = leBlogArticleService.articleListWithList(1, 10, 1L);
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 文章详细
     */
    @GetMapping("/{id}")
    public ResponseResult articleDetail(@PathVariable Long id){
        return ResponseResult.okResult(leBlogArticleService.articleDetail(id));
    }



}
