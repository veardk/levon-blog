package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.domain.entry.LeBlogCategory;
import com.levon.framework.domain.vo.LeBlogArticleDetailVO;
import com.levon.framework.domain.vo.LeBlogArticleListVO;
import com.levon.framework.domain.vo.LeBlogHotArticleVO;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.mapper.LeBlogArticleMapper;
import com.levon.framework.mapper.LeBlogCategoryMapper;
import com.levon.framework.service.LeBlogArticleService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.levon.framework.common.constants.ArticleConstants.ARTICLE_STATUS_PUBLISHED;

/**
 * @author leivik
 * @description 针对表【le_blog_article(文章表)】的数据库操作Service实现
 * @createDate 2025-02-20 17:47:34
 */
@Service
public class LeBlogArticleServiceImpl extends ServiceImpl<LeBlogArticleMapper, LeBlogArticle>
        implements LeBlogArticleService {

    @Autowired
    private LeBlogCategoryMapper leBlogCategoryMapper;

    @Autowired
    private LeBlogArticleMapper leBlogArticleMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取热门文章列表
     *
     * @return
     */
    @Override
    public List<LeBlogHotArticleVO> hotArticleList() {
        Page<LeBlogArticle> page = new Page<>(1, 10);

        LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)
                .orderByDesc(LeBlogArticle::getViewCount);

        page(page, wrapper);

        if (page.getRecords() != null && page.getRecords().size() > 0) {
            List<LeBlogArticle> hotArticles = page.getRecords();

            // redis -> 浏览量
            hotArticles.forEach(article -> {
                Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
                article.setViewCount(viewCount.longValue());
            });

            List<LeBlogHotArticleVO> leBlogHotArticleVos = BeanCopyUtils.copyBeanList(hotArticles, LeBlogHotArticleVO.class);
            return leBlogHotArticleVos;
        }
        return new ArrayList<>();
    }

    /**
     * 获取文章列表
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    @Override
    public PageVO articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<LeBlogArticle> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<LeBlogArticle> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null && categoryId > 0) {
            wrapper.eq(LeBlogArticle::getCategoryId, categoryId);
        }

        wrapper.eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)
                .orderByDesc(LeBlogArticle::getIsTop);

        page(page, wrapper);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            return new PageVO(Collections.emptyList(), 0L);
        }

        List<LeBlogArticle> leBlogArticleList = page.getRecords();
        List<LeBlogArticleListVO> leBlogArticleListVo = leBlogArticleList
                .stream()
                .map(article -> {
                    // 从redis读取访问量
                    Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
                    article.setViewCount(viewCount.longValue());

                    LeBlogArticleListVO articleVo = new LeBlogArticleListVO();
                    BeanUtils.copyProperties(article, articleVo);

                    // 获取分类名称并设置VO
                    LeBlogCategory category = leBlogCategoryMapper.selectById(article.getCategoryId());
                    Assert.notNull(category, "分类不存在" + article.getCategoryId());

                    articleVo.setCategoryName(category.getName());

                    return articleVo;
                })
                .collect(Collectors.toList());

        return new PageVO(leBlogArticleListVo, page.getTotal());
    }

    /**
     * 获取文章列表(IPage Mapper.xml->Vo方式)
     *
     * @return
     */
    public PageVO articleListByXml(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<LeBlogArticle> page = new Page<>(pageNum, pageSize);
        IPage<LeBlogArticleListVO> leBlogArticleListVoIPage = leBlogArticleMapper.articleListByXml(page, categoryId);
        Assert.notNull(leBlogArticleListVoIPage, "NULL");

        List<LeBlogArticleListVO> records = leBlogArticleListVoIPage.getRecords();
        return new PageVO(records, leBlogArticleListVoIPage.getTotal());
    }

    @Override
    public PageVO articleListWithPage(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<LeBlogArticle> page = new Page<>(pageNum, pageSize);
        Page<LeBlogArticleListVO> leBlogArticleListVoPage = leBlogArticleMapper.articleListWithPage(page, categoryId);
        Assert.notNull(leBlogArticleListVoPage, "NULL");

        List<LeBlogArticleListVO> records = leBlogArticleListVoPage.getRecords();

        return new PageVO(records, leBlogArticleListVoPage.getTotal());
    }

    @Override
    public PageVO articleListWithList(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<LeBlogArticle> page = new Page<>(pageNum, pageSize);
        List<LeBlogArticleListVO> leBlogArticleListVos = leBlogArticleMapper.articleListWithList(page, categoryId);
        Assert.notNull(leBlogArticleListVos, "NULL");

        return new PageVO(leBlogArticleListVos, Long.valueOf(leBlogArticleListVos.size()));
    }

    /**
     * 获取文章详情
     *
     * @return
     */
    @Override
    public LeBlogArticleDetailVO articleDetail(Long id) {

        LeBlogArticle leBlogArticle = leBlogArticleMapper.selectOne(new LambdaQueryWrapper<LeBlogArticle>()
                .eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)
                .eq(LeBlogArticle::getId, id)
        );

        Assert.notNull(leBlogArticle, "错误数据：" + id);

        // 从redis读取访问量
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        leBlogArticle.setViewCount(viewCount.longValue());

        LeBlogArticleDetailVO leBlogArticleDetailVo = BeanCopyUtils.copyBean(leBlogArticle, LeBlogArticleDetailVO.class);


        LeBlogCategory leBlogCategory = leBlogCategoryMapper.selectById(leBlogArticle.getCategoryId());
        Assert.notNull(leBlogCategory, "错误数据" + leBlogArticle.getCategoryId());

        leBlogArticleDetailVo.setCategoryName(leBlogCategory.getName());

        return leBlogArticleDetailVo;
    }

    @Override
    public void updateViewCount(Long id) {
        LeBlogArticle leBlogArticle = leBlogArticleMapper.selectOne(new LambdaQueryWrapper<LeBlogArticle>()
                .eq(LeBlogArticle::getId, id)
                .eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED));

        if (Objects.isNull(leBlogArticle)) {
            throw new SystemException(AppHttpCodeEnum.ARTICLE_NOT_FOUND);
        }

        try {
            String redisKey = "article:viewCount";
            redisCache.incrementCacheMapValue(redisKey, leBlogArticle.getId().toString(), 1);

        } catch (Exception e) {
            log.error("Update view count error for article id: {}", e);
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

    }

}




