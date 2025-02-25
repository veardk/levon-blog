package com.levon.framework.service;

import com.levon.framework.domain.vo.LeBlogArticleDetailVO;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.LeBlogHotArticleVO;
import com.levon.framework.domain.vo.PageVO;

import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_article(文章表)】的数据库操作Service
* @createDate 2025-02-20 17:47:34
*/
public interface LeBlogArticleService extends IService<LeBlogArticle> {

    /**
     * 获取热门文章列表
     * @return
     */
    List<LeBlogHotArticleVO> hotArticleList();

    /**
     * 获取文章列表(mybatis方式)
     * @return
     */
    PageVO articleList(Integer pageNum, Integer pageSize, Long categoryId);

    /**
     * 获取文章列表(IPage xml方式)
     */
    PageVO articleListByXml(Integer pageNum, Integer pageSize, Long categoryId);

    PageVO articleListWithPage(Integer pageNum, Integer pageSize, Long categoryId);

    PageVO articleListWithList(Integer pageNum, Integer pageSize, Long categoryId);

    LeBlogArticleDetailVO articleDetail(Long id);
}
