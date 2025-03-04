package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.constants.LinkConstants;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.domain.dto.AdminLinkCreateValidationDTO;
import com.levon.framework.domain.dto.AdminLinkUpdateValidationDTO;
import com.levon.framework.domain.dto.ChangeLinkStatusValidationDTO;
import com.levon.framework.domain.entry.LeBlogLink;
import com.levon.framework.domain.vo.AdminLinkVO;
import com.levon.framework.domain.vo.ClientLinkVO;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.mapper.LeBlogLinkMapper;
import com.levon.framework.service.LeBlogLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author leivik
 * @description 针对表【le_blog_link(友链)】的数据库操作Service实现
 * @createDate 2025-02-20 22:55:44
 */

@Service
@Slf4j
public class LeBlogLinkServiceImpl extends ServiceImpl<LeBlogLinkMapper, LeBlogLink>
        implements LeBlogLinkService {

    @Autowired
    private LeBlogLinkMapper leBlogLinkMapper;

    /**
     * 获取所有已审核通过的友链列表
     *
     * @return 包含所有已审核通过的友链的列表
     */
    @Override
    public List<ClientLinkVO> getAllLink() {
        LambdaQueryWrapper<LeBlogLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeBlogLink::getStatus, LinkConstants.AUDIT_STATUS_APPROVED);

        List<LeBlogLink> list = list(wrapper);

        if (Objects.nonNull(list)) {
            return BeanCopyUtils.copyBeanList(list, ClientLinkVO.class);
        }
        return Collections.emptyList();
    }

    /**
     * 获取友链的详细信息
     *
     * @param id 友链的ID
     * @return 包含友链详细信息的AdminLinkVO对象
     * @throws SystemException 如果找不到友链则抛出异常
     */
    @Override
    public AdminLinkVO detail(Long id) {
        return Optional.ofNullable(getById(id))
                .map(link -> BeanCopyUtils.copyBean(link, AdminLinkVO.class))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.LINK_NOT_FOUND, "Not Found Link ID: " + id));
    }


    /**
     * 分页查询友链列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示的记录数
     * @param name     友链名称（可选，用于模糊查询）
     * @param status   友链状态（可选，用于精确匹配）
     * @return 包含分页友链列表的PageVO对象
     */
    @Override
    public PageVO pageList(Integer pageNum, Integer pageSize, String name, String status) {
        Page<LeBlogLink> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LeBlogLink> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(name)) {
            wrapper.like(LeBlogLink::getName, name);
        }

        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(LeBlogLink::getStatus, status);
        }

        page(page, wrapper);
        if (page.getTotal() == 0) {
            return new PageVO(Collections.emptyList(), 0L);
        }

        List<AdminLinkVO> adminLinkVOS = BeanCopyUtils.copyBeanList(page.getRecords(), AdminLinkVO.class);

        return new PageVO(adminLinkVOS, page.getTotal());
    }

    /**
     * 编辑友情链接
     *
     * @param updateRequest 友情链接更新请求对象
     */
    @Override
    public void edit(AdminLinkUpdateValidationDTO updateRequest) {
        Optional.ofNullable(getById(updateRequest.getId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.LINK_NOT_FOUND, "未找到链接ID： " + updateRequest.getId()));

        LeBlogLink link = BeanCopyUtils.copyBean(updateRequest, LeBlogLink.class);
        if (!updateById(link)) {
            log.error("编辑友链名称为{}失败", updateRequest.getName());
            throw new SystemException(AppHttpCodeEnum.UPDATE_FAILED, "编辑友链名称为{}失败" + updateRequest.getName());
        }
        log.info("成功编辑友链名称为{}", updateRequest.getName());
    }

    /**
     * 新增友情链接
     *
     * @param createRequest 友情链接创建请求对象
     */
    @Override
    public void add(AdminLinkCreateValidationDTO createRequest) {
        if (isLinkExist(createRequest.getName())) {
            throw new SystemException(AppHttpCodeEnum.DATA_EXIST, "Link with name " + createRequest.getName() + " already exists.");
        }

        LeBlogLink link = BeanCopyUtils.copyBean(createRequest, LeBlogLink.class);
        if (!save(link)) {
            log.error("Failed to create the new link with name : {}", createRequest.getName());
            throw new SystemException(AppHttpCodeEnum.CREATE_FAILED, "Failed to create new link with name: " + createRequest.getName());
        }
        log.info("Successfully create the link : {}", createRequest.getName());
    }

    /**
     * 删除单个友情链接
     *
     * @param id 友情链接ID
     */
    @Override
    public void del(Long id) {
        Optional.ofNullable(getById(id))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.LINK_NOT_FOUND, "Not found link ID: " + id));

        if (!removeById(id)) {
            log.error("Failed to delete the link whit ID: {}", id);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the link whit ID: " + id);
        }
        log.info("Successfully delete link with ID: {}", id);
    }

    /**
     * 批量删除友情链接
     *
     * @param idList 友情链接ID列表
     */
    @Override
    public void baechDel(List<Long> idList) {
        if (Objects.nonNull(idList)) {
            idList.forEach(this::del);
        }
    }

    /**
     * 切换友情链接状态
     *
     * @param changeRequest 状态切换请求
     */
    @Override
    public void changeStatus(ChangeLinkStatusValidationDTO changeRequest) {
        Optional.ofNullable(getById(changeRequest.getId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.LINK_NOT_FOUND, "Not found link ID: " + changeRequest.getId()));

        LambdaUpdateWrapper<LeBlogLink> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.eq(LeBlogLink::getId, changeRequest.getId())
                .set(LeBlogLink::getStatus, changeRequest.getStatus());

        if (!update(updateWrapper)) {
            log.error("Failed to change status with ID : {}", changeRequest.getId());
            throw new SystemException(AppHttpCodeEnum.CHANGE_STATUS_ERROR, "Failed to change status with ID : " + changeRequest.getId());
        }
        log.info("Successfully change status with ID : {}", changeRequest.getId());
    }

    /**
     * 判断友链名称是否存在
     *
     * @param name 友链姓名
     * @return 友链存在放回true，否则返回false
     */
    private boolean isLinkExist(String name) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<LeBlogLink>()
                        .eq(LeBlogLink::getName, name)))
                .isPresent();
    }

}




