package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.constants.CategoryConstants;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.domain.dto.AdminCategoryCreateValidationDTO;
import com.levon.framework.domain.dto.AdminCategoryUpdateValidationDTO;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.domain.entry.LeBlogCategory;
import com.levon.framework.domain.vo.AdminCategoryListVO;
import com.levon.framework.domain.vo.ClientCategoryVO;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.mapper.LeBlogCategoryMapper;
import com.levon.framework.service.LeBlogArticleService;
import com.levon.framework.service.LeBlogCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author leivik
 * @description 针对表【le_blog_category(分类表)】的数据库操作Service实现
 * @createDate 2025-02-20 22:53:41
 */

@Service
@Slf4j
public class LeBlogCategoryServiceImpl extends ServiceImpl<LeBlogCategoryMapper, LeBlogCategory>
        implements LeBlogCategoryService {

    @Autowired
    private LeBlogCategoryMapper leBlogCategoryMapper;

    @Autowired
    private LeBlogArticleService leBlogArticleService;

    /**
     * 获取文章分类列表
     *
     * @return 分类列表结果
     */
    @Override
    public List<ClientCategoryVO> getCateGoryList() {
        List<LeBlogCategory> ctegoryList = leBlogCategoryMapper.selectList(new LambdaQueryWrapper<LeBlogCategory>()
                .eq(LeBlogCategory::getStatus, CategoryConstants.CATEGORY_STATUS_NORMAL));

        if (ctegoryList.isEmpty()) {
            return new ArrayList<>();
        }

        return BeanCopyUtils.copyBeanList(ctegoryList, ClientCategoryVO.class);
    }

    /**
     * 分页查询分类列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param name     分类名称查询条件
     * @param status   分类状态查询条件
     * @return 分类列表分页结果
     */
    @Override
    public PageVO list(Integer pageNum, Integer pageSize, String name, String status) {
        Page<LeBlogCategory> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<LeBlogCategory> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(name)) {
            wrapper.like(LeBlogCategory::getName, name);
        }
        if (StringUtils.isNotBlank(status)) {
            wrapper.like(LeBlogCategory::getStatus, status);
        }

        page(page, wrapper);
        if (page.getTotal() == 0) {
            return new PageVO(Collections.emptyList(), 0L);
        }

        List<AdminCategoryListVO> categoryVOList = BeanCopyUtils.copyBeanList(page.getRecords(), AdminCategoryListVO.class);

        return new PageVO(categoryVOList, page.getTotal());
    }


    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情VO
     */
    @Override
    public AdminCategoryListVO detail(Long id) {
        return Optional.ofNullable(getById(id))
                .map(category -> BeanCopyUtils.copyBean(category, AdminCategoryListVO.class))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + id));
    }

    /**
     * 更新分类信息
     *
     * @param updateRequest 分类更新请求对象
     */
    @Override
    public void edit(AdminCategoryUpdateValidationDTO updateRequest) {
        Optional.ofNullable(getById(updateRequest.getId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + updateRequest.getId()));

        LeBlogCategory category = BeanCopyUtils.copyBean(updateRequest, LeBlogCategory.class);
        if (!updateById(category)) {
            log.error("Failed to update the category with ID: {} ", updateRequest.getId());
            throw new SystemException(AppHttpCodeEnum.UPDATE_FAILED, "Failed to update the category with ID：" + updateRequest.getId());

        }
        log.info("Successfully updated category with ID: {} ", updateRequest.getId());
    }

    /**
     * 新增分类
     *
     * @param createRequest 分类创建请求对象
     */
    @Override
    public void add(AdminCategoryCreateValidationDTO createRequest) {
        if (isCategoryNameExist(createRequest.getName())) {
            throw new SystemException(AppHttpCodeEnum.DATA_EXIST, "The category name with \"" + createRequest.getName() + "\" already exists");
        }

        LeBlogCategory category = BeanCopyUtils.copyBean(createRequest, LeBlogCategory.class);

        if (!save(category)) {
            log.error("Failed to create the category with name : {} ", createRequest.getName());
            throw new SystemException(AppHttpCodeEnum.CREATE_FAILED, "Failed to create the category with name :  " + createRequest.getName());
        }
        log.info("Successfully create the category with name : {} " + createRequest.getName());
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    @Override
    public void del(Long id) {
        Optional.ofNullable(getById(id))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + id));

        // 绑定该分类的文章,赋值为0(默认分类)
        LambdaUpdateWrapper updateWrapper = new LambdaUpdateWrapper<LeBlogArticle>()
                .eq(LeBlogArticle::getCategoryId, id)
                .set(LeBlogArticle::getCategoryId, 0L);

        if (!leBlogArticleService.update(updateWrapper)) {
            log.error("修改为默认分类错误，分类ID {}", id);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "修改为默认分类错误，分类ID;" + id);
        }

        if (!removeById(id)) {
            log.error("Failed to delete the category with ID: {}", id);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the category with ID:" + id);
        }
        log.info("Successfully delete the category with ID: {}", id);

    }

    /**
     * 批量删除分类
     *
     * @param idList 分类ID列表
     */
    @Override
    public void batchDel(List<Long> idList) {
        if (Objects.nonNull(idList)) {
            idList.forEach(this::del);
        }
    }

    /**
     * 切换分类状态
     *
     * @param id 分类ID
     */
    @Override
    public void toggleStatus(Long id) {
        LeBlogCategory category = Optional.ofNullable(getById(id))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + id));

        category.setStatus(category.getStatus().equals("1") ? "0" : "1");

        if (!updateById(category)) {
            log.error("Failed to toggle category status with ID: {} ", id);
            throw new SystemException(AppHttpCodeEnum.UPDATE_FAILED, "Failed to toggle category status with ID：" + id);

        }
        log.info("Successfully toggle category status with ID: {} ", id);
    }

    /**
     * 判断分类姓名是否存在
     *
     * @param name 分类姓名
     * @return 分类存在放回true，否则返回false
     */
    private boolean isCategoryNameExist(String name) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<LeBlogCategory>()
                        .eq(LeBlogCategory::getName, name)))
                .isPresent();
    }

}




