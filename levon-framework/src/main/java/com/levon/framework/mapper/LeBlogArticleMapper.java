package com.levon.framework.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.levon.framework.domain.vo.ClientArticleListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_article(文章表)】的数据库操作Mapper
* @createDate 2025-02-20 17:47:34
* @Entity com.levon.domain.entry.LeBlogArticle
*/
@Mapper
public interface LeBlogArticleMapper extends BaseMapper<LeBlogArticle> {
    IPage<ClientArticleListVO> articleListByXml(Page<LeBlogArticle> page, @Param("categoryId") Long categoryId);

    Page<ClientArticleListVO> articleListWithPage(Page<LeBlogArticle> page, @Param("categoryId") Long categoryId);

    List<ClientArticleListVO> articleListWithList(Page<LeBlogArticle> page, @Param("categoryId") Long categoryId);

}




