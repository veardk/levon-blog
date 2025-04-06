package com.levon.framework.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.mapper.LeBlogArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.levon.framework.common.constants.ArticleConstants.ARTICLE_STATUS_PUBLISHED;

/**
 * 文章浏览量初始化加载器
 * 
 * @author leivik
 */
@Component
@Slf4j
public class ViewCountRunner implements CommandLineRunner {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LeBlogArticleMapper leBlogArticleMapper;

    private static final String ARTICLE_VIEW_COUNT_KEY = "article:viewCount";
    private static final int EXPIRE_TIME = 24; // 设置为24小时
    private static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.HOURS;

    @Override
    public void run(String... args) {
        try {
            log.info("开始初始化文章浏览量数据到Redis");
            
            // 1. 检查Redis中是否已有数据
            Map<String, Integer> existingData = redisCache.getCacheMap(ARTICLE_VIEW_COUNT_KEY);
            
            // 2. 从数据库加载全部发布状态的文章
            List<LeBlogArticle> leBlogArticles = Optional.ofNullable(leBlogArticleMapper.selectList(
                    new LambdaQueryWrapper<LeBlogArticle>()
                    .eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)))
                    .orElseThrow(() -> new SystemException(AppHttpCodeEnum.ARTICLE_NOT_FOUND));
            
            // 3. 合并数据，优先使用Redis中已有的数据
            Map<String, Integer> viewCountMap = new HashMap<>(leBlogArticles.size());
            
            for (LeBlogArticle article : leBlogArticles) {
                String articleId = article.getId().toString();
                
                // 如果Redis中有该文章的浏览量，优先使用Redis中的数据
                if (existingData.containsKey(articleId)) {
                    viewCountMap.put(articleId, existingData.get(articleId));
                } else {
                    // 否则使用数据库中的数据
                    viewCountMap.put(articleId, article.getViewCount().intValue());
                }
            }
            
            // 4. 更新到Redis并设置较长的过期时间
            redisCache.setCacheMap(ARTICLE_VIEW_COUNT_KEY, viewCountMap);
            redisCache.expire(ARTICLE_VIEW_COUNT_KEY, EXPIRE_TIME, EXPIRE_TIME_UNIT);
            
            log.info("文章浏览量数据初始化成功，共加载{}篇文章, 过期时间:{}小时", viewCountMap.size(), EXPIRE_TIME);
        } catch (Exception e) {
            log.error("初始化文章浏览量缓存失败", e);
        }
    }
}
