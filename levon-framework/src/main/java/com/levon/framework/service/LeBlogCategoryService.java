package com.levon.framework.service;

import com.levon.framework.domain.entry.LeBlogCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.LeBlogCategoryVO;

import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_category(分类表)】的数据库操作Service
* @createDate 2025-02-20 22:53:41
*/
public interface LeBlogCategoryService extends IService<LeBlogCategory> {

    /**
     * 获取文章分类列表
     * @return
     */
    List<LeBlogCategoryVO> getCateGoryList();
}
