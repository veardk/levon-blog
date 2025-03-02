package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.constants.CategoryConstants;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.domain.dto.AdminCategoryCreateValidationDTO;
import com.levon.framework.domain.dto.AdminCategoryUpdateValidationDTO;
import com.levon.framework.domain.entry.LeBlogCategory;
import com.levon.framework.domain.vo.AdminCategoryListVO;
import com.levon.framework.domain.vo.ClientCategoryVO;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.mapper.LeBlogCategoryMapper;
import com.levon.framework.service.LeBlogCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 获取所有文章分类
     *
     * @return
     */
    @Override
    public List<ClientCategoryVO> getCateGoryList() {
        List<LeBlogCategory> ctegoryList = leBlogCategoryMapper.selectList(new LambdaQueryWrapper<LeBlogCategory>()
                .eq(LeBlogCategory::getStatus, CategoryConstants.CATEGORY_STATUS_NORMAL));

        if (ctegoryList.isEmpty() || ctegoryList.size() == 0) {
            return new ArrayList<>();
        }

        return BeanCopyUtils.copyBeanList(ctegoryList, ClientCategoryVO.class);
    }

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

    @Override
    public AdminCategoryListVO detail(Long id) {
        return Optional.ofNullable(getById(id))
                .map(category -> BeanCopyUtils.copyBean(category, AdminCategoryListVO.class))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + id));
    }

    @Override
    public void update(AdminCategoryUpdateValidationDTO updateRequest) {
        Optional.ofNullable(getById(updateRequest.getId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + updateRequest.getId()));

        LeBlogCategory category = BeanCopyUtils.copyBean(updateRequest, LeBlogCategory.class);
        if (!updateById(category)) {
            log.error("Failed to update the category with ID: {} ", updateRequest.getId());
            throw new SystemException(AppHttpCodeEnum.UPDATE_FAILED, "Failed to update the category with ID：" + updateRequest.getId());

        }
        log.info("Successfully updated category with ID: {} ", updateRequest.getId());
    }

    @Override
    public void add(AdminCategoryCreateValidationDTO createRequest) {
        if (isCategoryExist(createRequest.getName())) {
            throw new SystemException(AppHttpCodeEnum.DATA_EXIST, "The category name with " + createRequest.getName() + " already exists");
        }

        LeBlogCategory category = BeanCopyUtils.copyBean(createRequest, LeBlogCategory.class);

        if (!save(category)) {
            log.error("Failed to create the category with name : {} ", createRequest.getName());
            throw new SystemException(AppHttpCodeEnum.CREATE_FAILED, "Failed to create the category with name :  " + createRequest.getName());
        }
        log.info("Successfully create the category with name : {} " + createRequest.getName());
    }

    @Override
    public void del(Long id) {
        Optional.ofNullable(getById(id))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + id));

        if (!removeById(id)) {
            log.error("Failed to delete the category with ID: {}", id);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the category with ID:" + id);
        }
        log.info("Successfully delete the category with ID: {}", id);

        // TODO 该分类删除后，填写对应文章的分类赋值为空或-1或0
    }

    @Override
    public void delIds(List<Long> idList) {
        validateIdsExist(idList);
        if (!removeBatchByIds(idList)) {
            log.error("Failed to delete the category with ID: {}", idList.toString());
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the category with ID:" + idList.toString());
        }
        log.info("Successfully delete the category with ID: {}", idList.toString());

        // TODO 该分类删除后，填写对应文章的分类赋值为空或-1或0
    }

    @Override
    public void changeStatus(Long id) {
        // TODO 修改状态
    }


    /**
     * 判断分类是否存在
     *
     * @param name 分类姓名
     * @return 分类存在放回true，否则返回false
     */
    private boolean isCategoryExist(String name) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<LeBlogCategory>()
                        .eq(LeBlogCategory::getName, name)))
                .isPresent();
    }

    private void validateIdsExist(List<Long> ids) {
        List<LeBlogCategory> existingCategory = leBlogCategoryMapper.selectBatchIds(ids);
        if (existingCategory.size() != ids.size()) {
            // 有不存在的 ID
            Set<Long> existingIds = existingCategory.stream()
                    .map(LeBlogCategory::getId)
                    .collect(Collectors.toSet());
            List<Long> nonExistentIds = ids.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toList());
            log.error("Category with IDs {} not found.", nonExistentIds);
            throw new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Category with IDs " + nonExistentIds + " not found.");
        }
    }
}




