package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.domain.dto.AdminArticleCreateValidationDTO;
import com.levon.framework.domain.dto.AdminArticleUpdateValidationDTO;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.domain.entry.LeBlogArticleTag;
import com.levon.framework.domain.vo.*;
import com.levon.framework.mapper.LeBlogArticleMapper;
import com.levon.framework.service.LeBlogArticleService;
import com.levon.framework.service.LeBlogArticleTagService;
import com.levon.framework.service.LeBlogCategoryService;
import com.levon.framework.service.LeBlogTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.levon.framework.common.constants.ArticleConstants.ARTICLE_STATUS_PUBLISHED;

/**
 * @author leivik
 * @description 针对表【le_blog_article(文章表)】的数据库操作Service实现
 * @createDate 2025-02-20 17:47:34
 */
@Service
@Slf4j
public class LeBlogArticleServiceImpl extends ServiceImpl<LeBlogArticleMapper, LeBlogArticle>
        implements LeBlogArticleService {

    @Autowired
    private LeBlogCategoryService leBlogCategoryService;

    @Autowired
    private LeBlogArticleMapper leBlogArticleMapper;

    @Autowired
    private LeBlogArticleTagService leBlogArticleTagService;

    @Autowired
    private LeBlogTagService leBlogTagService;


    @Autowired
    private RedisCache redisCache;

    private static final String ARTICLE_VIEW_COUNT_KEY = "article:viewCount"; // view count redis key

    /**
     * 获取热门文章列表
     *
     * @return 热门文章VO
     */
    @Override
    public List<ClientHotArticleVO> hotArticleList() {
        Page<LeBlogArticle> page = new Page<>(1, 10);

        LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)
                .orderByDesc(LeBlogArticle::getViewCount);

        page(page, wrapper);

        if (page.getRecords() != null && page.getRecords().size() > 0) {
            List<LeBlogArticle> hotArticles = page.getRecords();

            // redis -> 浏览量
            try {
                hotArticles.forEach(this::setViewCountFromRedis);
            } catch (Exception e) {
                log.error("更新文章浏览量发生未知异常", e);
                throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "系统繁忙，请稍后重试");
            }


            return BeanCopyUtils.copyBeanList(hotArticles, ClientHotArticleVO.class);
        }
        return new ArrayList<>();
    }

    /**
     * 前台获取文章列表
     *
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @param categoryId 分类ID
     * @return PageVO 分页封装返回
     */
    @Override
    public PageVO articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<LeBlogArticle> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null && categoryId > 0) {
            Optional.ofNullable(leBlogCategoryService.getById(categoryId))
                    .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + categoryId));

            wrapper.eq(LeBlogArticle::getCategoryId, categoryId);
        }

        wrapper.eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)
                .orderByDesc(LeBlogArticle::getIsTop);

        page(page, wrapper);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            return new PageVO(Collections.emptyList(), 0L);
        }

        List<LeBlogArticle> leBlogArticleList = page.getRecords();
        List<ClientArticleListVO> leBlogArticleListVo = leBlogArticleList
                .stream()
                .map(article -> {
                    // 从redis读取访问量
                    setViewCountFromRedis(article);

                    ClientArticleListVO articleVo = new ClientArticleListVO();
                    BeanUtils.copyProperties(article, articleVo);

                    // 获取分类名称并设置VO
                    String categoryName = getCategoryNameByArticle(article);
                    articleVo.setCategoryName(categoryName);

                    return articleVo;
                })
                .collect(Collectors.toList());

        return new PageVO(leBlogArticleListVo, page.getTotal());
    }

    /**
     * 前台：根据文章ID查询文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Override
    public ClientArticleDetailVO articleDetail(Long id) {
        LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<LeBlogArticle>()
                .eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)
                .eq(LeBlogArticle::getId, id);

        LeBlogArticle leBlogArticle = Optional.ofNullable(leBlogArticleMapper.selectOne(wrapper))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Article ID: " + id));

        // 从redis读取访问量
        setViewCountFromRedis(leBlogArticle);

        ClientArticleDetailVO leBlogArticleDetailVo = BeanCopyUtils.copyBean(leBlogArticle, ClientArticleDetailVO.class);

        // 分类id->拿到分类名称 赋值
        String categoryName = getCategoryNameByArticle(leBlogArticle);
        leBlogArticleDetailVo.setCategoryName(categoryName);

        return leBlogArticleDetailVo;
    }

    /**
     * 更新文章浏览量
     *
     * @param id 文章ID
     */
    @Override
    public void updateViewCount(Long id) {
        LeBlogArticle leBlogArticle = leBlogArticleMapper.selectOne(new LambdaQueryWrapper<LeBlogArticle>()
                .eq(LeBlogArticle::getId, id)
                .eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED));

        if (Objects.isNull(leBlogArticle)) {
            throw new SystemException(AppHttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        try {
            redisCache.incrementCacheMapValue(ARTICLE_VIEW_COUNT_KEY, leBlogArticle.getId().toString(), 1);
        } catch (Exception e) {
            log.error("Update view count error for article id: {}", id);
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "Update view count error for article id:  " + id);
        }

    }

    /**
     * 新增文章
     *
     * @param createRequest 文章创建请求对象
     */
    @Override
    public void add(AdminArticleCreateValidationDTO createRequest) {

        // 判断标题是否已存在
        LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<LeBlogArticle>()
                .eq(LeBlogArticle::getTitle, createRequest.getTitle());

        if (getOne(wrapper) != null) {
            throw new SystemException(AppHttpCodeEnum.DATA_EXIST, "标题已重复: " + createRequest.getTitle() + " ,请重新命名!  ");
        }

        // 判断分类是否为有效数据
        Optional.ofNullable(leBlogCategoryService.getById(createRequest.getCategoryId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "the category with id: " + createRequest.getCategoryId() + " didn't found"));

        // 判断标签是否为有效数据
        leBlogTagService.validateIdsExist(createRequest.getTags());

        LeBlogArticle article = BeanCopyUtils.copyBean(createRequest, LeBlogArticle.class);

        if (!save(article)) {
            log.error("Failed to create the article with title : {} ", createRequest.getTitle());
            throw new SystemException(AppHttpCodeEnum.CREATE_FAILED, "Failed to create the article title with : " + createRequest.getTitle());
        }

        //  插入 文章与标签的关联表
        if (!createRequest.getTags().isEmpty()) {
            List<LeBlogArticleTag> articleTag = createRequest.getTags()
                    .stream()
                    .map(tag -> new LeBlogArticleTag(article.getId(), tag))
                    .collect(Collectors.toList());

            if (!leBlogArticleTagService.saveBatch(articleTag)) {
                throw new SystemException(AppHttpCodeEnum.CREATE_FAILED, "文章与标签关联失败：" + articleTag.toString());
            }
            log.info("文章与标签关联成功 : {} ", articleTag.toString());
        }

        log.info("Successfully create the article with title : {}", createRequest.getTitle());
    }

    /**
     * 后台分页查询文章列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param title    文章标题查询条件
     * @param summary  文章摘要查询条件
     * @return 文章列表分页结果
     */
    @Override
    public PageVO getList(Integer pageNum, Integer pageSize, String title, String summary) {
        Page<LeBlogArticle> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(title)) {
            wrapper.like(LeBlogArticle::getTitle, title);
        }

        if (StringUtils.isNotBlank(summary)) {
            wrapper.like(LeBlogArticle::getSummary, summary);
        }

        page(page, wrapper);

        if (page.getTotal() == 0) {
            return new PageVO(Collections.emptyList(), page.getTotal());
        }

        List<LeBlogArticle> articleList = page.getRecords();
        List<AdminArticleListVO> adminArticleListVOS = articleList
                .stream()
                .map(article -> {
                    // 从redis读取访问量并赋值
                    setViewCountFromRedis(article);

                    AdminArticleListVO articleVo = new AdminArticleListVO();
                    BeanUtils.copyProperties(article, articleVo);

                    // 获取分类名称并赋值
                    String categoryName = getCategoryNameByArticle(article);
                    articleVo.setCategoryName(categoryName);

                    // 获取文章tags并赋值
                    List<Long> tagsList = getTagsByArticleId(article.getId());
                    articleVo.setTags(tagsList);

                    return articleVo;
                })
                .collect(Collectors.toList());

        return new PageVO(adminArticleListVOS, page.getTotal());
    }

    /**
     * 后台根据文章ID查询文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Override
    public AdminArticleDetailVO detail(Long id) {

        LeBlogArticle article = Optional.ofNullable(getById(id))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Article ID: " + id));

        // 从redis读取访问量并赋值
        setViewCountFromRedis(article);

        AdminArticleDetailVO adminArticleDetailVO = BeanCopyUtils.copyBean(article, AdminArticleDetailVO.class);

        // 分类id->拿到分类名称 赋值
        String categoryName = getCategoryNameByArticle(article);
        adminArticleDetailVO.setCategoryName(categoryName);

        // 获取文章tags
        List<Long> tagsList = getTagsByArticleId(article.getId());
        adminArticleDetailVO.setTags(tagsList);

        return adminArticleDetailVO;
    }

    /**
     * 编辑文章
     *
     * @param updateRequest 文章更新请求对象
     */
    @Override
    public void edit(AdminArticleUpdateValidationDTO updateRequest) {
        Long originalArticleId = updateRequest.getId();

        // 判断是否有效文章
        LeBlogArticle originalArticle = Optional.ofNullable(getById(originalArticleId))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "文章不存在 ID : " + originalArticleId));

        // 判断标题是否已存在
        if (!originalArticle.getTitle().equals(updateRequest.getTitle())) {
            LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<LeBlogArticle>()
                    .eq(LeBlogArticle::getTitle, updateRequest.getTitle());
            if (getOne(wrapper) != null) {
                throw new SystemException(AppHttpCodeEnum.DATA_EXIST, "标题已重复: " + updateRequest.getTitle() + " ,请重新命名!  ");
            }
        }

        // 判断分类是否为有效数据
        Optional.ofNullable(leBlogCategoryService.getById(updateRequest.getCategoryId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "the category with id: " + updateRequest.getCategoryId() + " didn't found"));

        // 判断标签是否为有效数据
        leBlogTagService.validateIdsExist(updateRequest.getTags());

        LeBlogArticle article = BeanCopyUtils.copyBean(updateRequest, LeBlogArticle.class);

        // 若标签有变动,删除该文章在文章标签关联表中原有的关联，并插入新设置的关联
        updateArticleTagAssociation(originalArticleId, updateRequest.getTags());

        if (!updateById(article)) {
            log.error("Failed to update the article with title : {} ", updateRequest.getTitle());
            throw new SystemException(AppHttpCodeEnum.UPDATE_FAILED, "Failed to update the article title with : " + updateRequest.getTitle());
        }
        log.info("Successfully update the article with title : {}", updateRequest.getTitle());
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    @Override
    public void del(Long id) {
        // 判断是否有效文章
        Optional.ofNullable(getById(id))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "文章不存在 ID : " + id));

        // 删除关联表中与文章关联的数据
        if (!leBlogArticleTagService.remove(new LambdaQueryWrapper<LeBlogArticleTag>().eq(LeBlogArticleTag::getArticleId, id))) {
            log.error("Failed to delete the Tag with ID: {}", id);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the Tag with  ID: " + id);
        }

        // 删除文章
        if (!removeById(id)) {
            log.error("Failed to delete the article whit ID: {}", id);
            throw new SystemException(AppHttpCodeEnum.DELETE_FAILED, "Failed to delete the article with ID: " + id);
        }
        log.info("Successfully delete article with ID: {}", id);
    }

    /**
     * 批量删除文章
     *
     * @param delIdList 待删除的文章ID列表
     */
    @Override
    public void batchDel(List<Long> delIdList) {
        if (Objects.nonNull(delIdList)) {
            delIdList.forEach(this::del);
        }
    }

    /**
     * 更新文章标签关联
     *
     * @param articleId 更新的文章Id
     * @param newTagIds 新设置的标签ID列表
     */
    private void updateArticleTagAssociation(Long articleId, List<Long> newTagIds) {
        // 获取文章当前关联的标签ID
        List<Long> currentTagIds = getTagsByArticleId(articleId);
        // newTagIds:2,3
        // 计算需要新增和删除的标签ID
        List<Long> tagsToAdd = new ArrayList<>(newTagIds);
        tagsToAdd.removeAll(currentTagIds);

        List<Long> tagsToRemove = new ArrayList<>(currentTagIds);
        tagsToRemove.removeAll(newTagIds);

        // 执行标签关联更新
        if (!tagsToAdd.isEmpty() || !tagsToRemove.isEmpty()) {
            // 1. 删除需要删除的标签关联
            leBlogArticleTagService.remove(new LambdaQueryWrapper<LeBlogArticleTag>()
                    .eq(LeBlogArticleTag::getArticleId, articleId)
                    .in(LeBlogArticleTag::getTagId, tagsToRemove));

            // 2. 插入需要新增的标签关联
            List<LeBlogArticleTag> articleTags = tagsToAdd.stream()
                    .map(tagId -> new LeBlogArticleTag(articleId, tagId))
                    .collect(Collectors.toList());

            if (!leBlogArticleTagService.saveBatch(articleTags)) {
                throw new SystemException(AppHttpCodeEnum.CREATE_FAILED, "文章与标签关联失败：" + articleTags);
            }

            log.info("文章与标签关联更新成功 : {}", articleTags);
        } else {
            log.info("文章标签未发生变化，无需更新");
        }
    }

    /**
     * 通过文章Id获取文章标签列表
     *
     * @param articleId 当前文章Id
     * @return List<String> 标签列表
     */
    @NotNull
    private List<Long> getTagsByArticleId(Long articleId) {
        List<Long> tagsList = leBlogArticleTagService.list(new LambdaQueryWrapper<LeBlogArticleTag>()
                        .eq(LeBlogArticleTag::getArticleId, articleId))
                .stream()
                .map(LeBlogArticleTag::getTagId)
                .collect(Collectors.toList());
        return tagsList;
    }

    /**
     * 通过文章获取，该文章分类名称
     *
     * @param article 文章
     * @return 文章分类名称
     */
    @NotNull
    private String getCategoryNameByArticle(LeBlogArticle article) {
        return Optional.ofNullable(leBlogCategoryService.getById(article.getCategoryId()))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.DATA_NOT_FOUND, "Not Found Category ID: " + article.getCategoryId()))
                .getName();
    }

    /**
     * 通过文章，从redis读取文章访问量并赋值
     *
     * @param article 当前文章
     */
    private void setViewCountFromRedis(LeBlogArticle article) {
        try {
            Integer viewCount = redisCache.getCacheMapValue(ARTICLE_VIEW_COUNT_KEY, article.getId().toString());
            // 如果 Redis 中无数据，设置默认值 0
            article.setViewCount(viewCount != null ? viewCount.longValue() : 0L);
        } catch (Exception e) {
            Long articleId = article.getId();
            log.error("更新文章浏览量异常 | 文章ID: {} | 错误信息: {}", articleId, e.getMessage(), e);
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, String.format("更新文章浏览量异常 (文章ID: %s)", articleId));
        }
    }

}




