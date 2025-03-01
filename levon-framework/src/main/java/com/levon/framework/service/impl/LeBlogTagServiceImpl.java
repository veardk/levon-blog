package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.domain.dto.TagCreateValidationDTO;
import com.levon.framework.domain.dto.TagUpdateValidationDTO;
import com.levon.framework.domain.entry.LeBlogTag;
import com.levon.framework.domain.vo.LeBlogTagVO;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.service.LeBlogTagService;
import com.levon.framework.mapper.LeBlogTagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leivik
 * @description 针对表【le_blog_tag(标签)】的数据库操作Service实现
 * @createDate 2025-02-28 14:03:47
 */
@Service
@Slf4j
public class LeBlogTagServiceImpl extends ServiceImpl<LeBlogTagMapper, LeBlogTag>
        implements LeBlogTagService {
    @Autowired
    private LeBlogTagMapper leBlogTagMapper;

    /**
     * 获取标签列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页显示的记录数，默认为10
     * @param name     可选过滤参数，根据标签名称过滤
     * @param remark   可选过滤参数，根据备注信息过滤
     * @return PageVO 包含符合条件的标签列表
     */
    @Override
    public PageVO getTagList(Integer pageNum, Integer pageSize, String name, String remark) {
        Page<LeBlogTag> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<LeBlogTag> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(name)) {
            wrapper.like(LeBlogTag::getName, name);
        }
        if (StringUtils.isNotBlank(remark)) {
            wrapper.like(LeBlogTag::getRemark, remark);
        }

        page(page, wrapper);
        if (page.getTotal() == 0) {
            return new PageVO(Collections.emptyList(), 0L);
        }

        List<LeBlogTag> tagList = page.getRecords();
        List<LeBlogTagVO> leBlogTagVOS = BeanCopyUtils.copyBeanList(tagList, LeBlogTagVO.class);

        return new PageVO(leBlogTagVOS, page.getTotal());
    }

    /**
     * 根据 ID 获取标签详细信息
     *
     * @param id 标签的唯一标识符
     * @return 返回包含标签详细信息的对象
     */
    @Override
    public LeBlogTagVO getTagDetail(Long id) {
        return Optional.ofNullable(getById(id))
                .map(tag -> BeanCopyUtils.copyBean(tag, LeBlogTagVO.class))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.TAG_NOT_FOUND, "Not Found Tag ID: " + id));
    }

    /**
     * 更新标签信息
     *
     * @param updateRequest 标签更新请求对象，包含更新的标签信息
     */
    @Override
    public void updateTag(TagUpdateValidationDTO updateRequest) {
        Optional.ofNullable(getById(updateRequest.getId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.TAG_NOT_FOUND, "Not Found Tag ID: " + updateRequest.getId()));

        LeBlogTag leBlogTag = BeanCopyUtils.copyBean(updateRequest, LeBlogTag.class);

        if (!updateById(leBlogTag)) {
            log.error("Failed to update the tag with ID: {}", updateRequest.getId());
            throw new SystemException(AppHttpCodeEnum.UPDATE_FAILED, "Failed to update the tag with ID：" + updateRequest.getId());

        }
        log.info("Successfully updated tag with ID: {}", updateRequest.getId());
    }

    /**
     * 删除单个标签
     *
     * @param id 标签的唯一标识符
     */
    @Override
    public void deleteTag(Long id) {
        Optional.ofNullable(getById(id))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.TAG_NOT_FOUND, "Not found Tag ID:" + id));

        if (!removeById(id)) {
            log.error("Failed to delete the tag whit Tag ID: {}", id);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the tag whit Tag ID: " + id);
        }
        log.info("Successfully delete tag with ID: {}", id);
    }

    /**
     * 批量删除标签
     *
     * @param ids 标签的唯一标识符列表
     */
    @Override
    public void deleteTags(List<Long> ids) {
        validateIdsExist(ids);

        if (!removeBatchByIds(ids)) {
            log.error("Failed to delete the tag whit Tag ID: {}", ids);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the tag whit Tag ID: " + ids);
        }
        log.info("Successfully delete tag with ID: {}", ids);
    }

    /**
     * 验证给定的标签 ID 列表中的每个 ID 是否存在于数据库中。
     *
     * 如果列表中有任何 ID 不存在，将记录错误并抛出异常。
     *
     * @param ids 标签的唯一标识符列表
     * @throws SystemException 如果存在无效的标签 ID，则抛出该异常
     */
    private void validateIdsExist(List<Long> ids) {
        List<LeBlogTag> existingTags = leBlogTagMapper.selectBatchIds(ids);
        if (existingTags.size() != ids.size()) {
            // 有不存在的 ID
            Set<Long> existingIds = existingTags.stream()
                    .map(LeBlogTag::getId)
                    .collect(Collectors.toSet());
            List<Long> nonExistentIds = ids.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toList());
            log.error("Failed to delete tags with IDs: {}", nonExistentIds);
            throw new SystemException(AppHttpCodeEnum.TAG_NOT_FOUND, "Tags with IDs " + nonExistentIds + " not found.");
        }
    }

    /**
     * 创建新的标签
     *
     * @param createRequest 标签创建请求对象，包含新标签的信息
     */
    @Override
    public void createTag(TagCreateValidationDTO createRequest) {
        if (isTagExist(createRequest.getName())) {
            throw new SystemException(AppHttpCodeEnum.TAG_EXIST, "Tag with name '" + createRequest.getName() + "' already exists.");
        }

        LeBlogTag tag = BeanCopyUtils.copyBean(createRequest, LeBlogTag.class);
        if (!save(tag)) {
            log.error("Failed to create the new tag with name : {}", createRequest.getName());
            throw new SystemException(AppHttpCodeEnum.CREATE_FAILED, "Failed to create new tag with name: " + createRequest.getName());
        }
        log.info("Successfully create tag :", createRequest.getName());
    }

    /**
     * 判断标签是否存在
     *
     * @param name 标签姓名
     * @return 标签存在放回true，否则返回false
     */
    private boolean isTagExist(String name) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<LeBlogTag>()
                        .eq(LeBlogTag::getName, name)))
                .isPresent();
    }

}




