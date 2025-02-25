package com.levon.framework.mapper;

import com.levon.framework.domain.entry.LeBlogCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_category(分类表)】的数据库操作Mapper
* @createDate 2025-02-20 22:53:41
* @Entity com.levon.framework.domain.entry.LeBlogCategory
*/
@Mapper
public interface LeBlogCategoryMapper extends BaseMapper<LeBlogCategory> {

    /**
     * 获取文章分类列表
     * @return
     */
    List<LeBlogCategory> getCategoryList();
}




