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
     *
     * @return 分类列表结果
     */
    List<ClientCategoryVO> getCateGoryList();

    /**
     * 分页查询分类列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param name     分类名称查询条件
     * @param status   分类状态查询条件
     * @return 分类列表分页结果
     */
    PageVO list(Integer pageNum, Integer pageSize, String name, String status);

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情VO
     */
    AdminCategoryListVO detail(Long id);

    /**
     * 更新分类信息
     *
     * @param updateRequest 分类更新请求对象
     */
    void edit(AdminCategoryUpdateValidationDTO updateRequest);

    /**
     * 新增分类
     *
     * @param createRequest 分类创建请求对象
     */
    void add(AdminCategoryCreateValidationDTO createRequest);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void del(Long id);

    /**
     * 批量删除分类
     *
     * @param idList 分类ID列表
     */
    void batchDel(List<Long> idList);

    /**
     * 切换分类状态
     *
     * @param id 分类ID
     */
    void toggleStatus(Long id);

}
