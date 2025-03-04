package com.levon.framework.service;

import com.levon.framework.domain.dto.AdminTagCreateValidationDTO;
import com.levon.framework.domain.dto.AdminTagUpdateValidationDTO;
import com.levon.framework.domain.entry.LeBlogTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.AdminTagListVO;
import com.levon.framework.domain.vo.AdminTagVO;
import com.levon.framework.domain.vo.PageVO;

import java.util.List;

/**
 * @author leivik
 * @description 针对表【le_blog_tag(标签)】的数据库操作Service
 * @createDate 2025-02-28 14:03:47
 */
public interface LeBlogTagService extends IService<LeBlogTag> {

    /**
     * 获取标签列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页显示的记录数，默认为10
     * @param name     可选过滤参数，根据标签名称过滤
     * @param remark   可选过滤参数，根据备注信息过滤
     * @return PageVO 包含符合条件的标签列表
     */
    PageVO getTagList(Integer pageNum, Integer pageSize, String name, String remark);

    /**
     * 根据 ID 获取标签详细信息
     *
     * @param id 标签的唯一标识符
     * @return 返回包含标签详细信息的对象
     */
    AdminTagListVO getTagDetail(Long id);

    /**
     * 更新标签信息
     *
     * @param updateRequest 标签更新请求对象，包含更新的标签信息
     */
    void updateTag(AdminTagUpdateValidationDTO updateRequest);

    /**
     * 删除单个标签
     *
     * @param id 标签的唯一标识符
     */
    void deleteTag(Long id);

    /**
     * 批量删除标签
     *
     * @param ids 标签的唯一标识符列表
     */
    void beachDel(List<Long> ids);

    /**
     * 判断标签id 列表是否存在数据库中
     *
     * @param ids 标签列表
     */
    void validateIdsExist(List<Long> ids);

    /**
     * 创建新的标签
     *
     * @param createRequest 标签创建请求对象，包含新标签的信息
     */
    void createTag(AdminTagCreateValidationDTO createRequest);

    /**
     * 获取所有标签
     *
     * @return
     */
    List<AdminTagVO> getAllTag();
}
