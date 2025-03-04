package com.levon.framework.service;

import com.levon.framework.domain.dto.AdminArticleCreateValidationDTO;
import com.levon.framework.domain.dto.AdminArticleUpdateValidationDTO;
import com.levon.framework.domain.vo.AdminArticleDetailVO;
import com.levon.framework.domain.vo.ClientArticleDetailVO;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.ClientHotArticleVO;
import com.levon.framework.domain.vo.PageVO;

import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_article(文章表)】的数据库操作Service
* @createDate 2025-02-20 17:47:34
*/
public interface LeBlogArticleService extends IService<LeBlogArticle> {

    /**
     * 获取热门文章列表
     * @return
     */
    List<ClientHotArticleVO> hotArticleList();

    /**
     * 前台获取文章列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param categoryId 分类ID
     * @return PageVO 分页封装返回
     */
    PageVO articleList(Integer pageNum, Integer pageSize, Long categoryId);

    /**
     * 根据文章ID查询文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    ClientArticleDetailVO articleDetail(Long id);

    /**
     * 更新文章浏览量
     * @param id 文章ID
     */
    void updateViewCount(Long id);

    /**
     * 新增文章
     * @param createRequest 文章创建请求对象
     */
    void add(AdminArticleCreateValidationDTO createRequest);

    /**
     * 后台分页查询文章列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param title 文章标题查询条件
     * @param summary 文章摘要查询条件
     * @return 文章列表分页结果
     */
    PageVO getList(Integer pageNum, Integer pageSize, String title, String summary);

    /**
     * 后台根据文章ID查询文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    AdminArticleDetailVO detail(Long id);

    /**
     * 编辑文章
     * @param updateRequest 文章更新请求对象
     */
    void edit(AdminArticleUpdateValidationDTO updateRequest);

    /**
     * 删除文章
     * @param id 文章ID
     */
    void del(Long id);

    /**
     * 批量删除文章
     * @param delIdList 待删除的文章ID列表
     */
    void batchDel(List<Long> delIdList);
}
