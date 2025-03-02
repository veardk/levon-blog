package com.levon.framework.service;

import com.levon.framework.domain.dto.AdminCategoryCreateValidationDTO;
import com.levon.framework.domain.dto.AdminCategoryUpdateValidationDTO;
import com.levon.framework.domain.entry.LeBlogCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.AdminCategoryListVO;
import com.levon.framework.domain.vo.ClientCategoryVO;
import com.levon.framework.domain.vo.PageVO;

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
    List<ClientCategoryVO> getCateGoryList();

    PageVO list(Integer pageNum, Integer pageSize, String name, String status);

    AdminCategoryListVO detail(Long id);

    void update(AdminCategoryUpdateValidationDTO updateRequest);

    void add(AdminCategoryCreateValidationDTO createRequest);

    void del(Long id);

    void delIds(List<Long> idList);

    void changeStatus(Long id);

}
