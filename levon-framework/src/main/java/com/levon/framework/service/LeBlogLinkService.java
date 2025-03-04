package com.levon.framework.service;

import com.levon.framework.domain.dto.AdminLinkCreateValidationDTO;
import com.levon.framework.domain.dto.AdminLinkUpdateValidationDTO;
import com.levon.framework.domain.dto.ChangeLinkStatusValidationDTO;
import com.levon.framework.domain.entry.LeBlogLink;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.AdminLinkVO;
import com.levon.framework.domain.vo.ClientLinkVO;
import com.levon.framework.domain.vo.PageVO;

import java.util.List;

/**
 * @author leivik
 * @description 针对表【le_blog_link(友链)】的数据库操作Service
 * @createDate 2025-02-20 22:55:44
 */
public interface LeBlogLinkService extends IService<LeBlogLink> {

    /**
     * 获取所有友情链接
     *
     * @return 友情链接列表
     */
    List<ClientLinkVO> getAllLink();

    /**
     * 获取友情链接详情
     *
     * @param id 友情链接ID
     * @return 友情链接详情VO
     */
    AdminLinkVO detail(Long id);

    /**
     * 分页查询友情链接
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param name     友情链接名称查询条件
     * @param status   友情链接状态查询条件
     * @return 分页结果
     */
    PageVO pageList(Integer pageNum, Integer pageSize, String name, String status);

    /**
     * 编辑友情链接
     *
     * @param updateRequest 友情链接更新请求对象
     */
    void edit(AdminLinkUpdateValidationDTO updateRequest);

    /**
     * 新增友情链接
     *
     * @param createRequest 友情链接创建请求对象
     */
    void add(AdminLinkCreateValidationDTO createRequest);

    /**
     * 删除单个友情链接
     *
     * @param id 友情链接ID
     */
    void del(Long id);

    /**
     * 批量删除友情链接
     *
     * @param idList 友情链接ID列表
     */
    void baechDel(List<Long> idList);

    /**
     * 切换友情链接状态
     *
     * @param changeRequest 状态切换请求
     */
    void changeStatus(ChangeLinkStatusValidationDTO changeRequest);
}
