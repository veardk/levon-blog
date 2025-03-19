package com.levon.framework.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.mapper.LeBlogArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import static com.levon.framework.common.constants.ArticleConstants.ARTICLE_STATUS_PUBLISHED;

@Component
public class ViewCountRunner implements CommandLineRunner {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LeBlogArticleMapper leBlogArticleMapper;

    @Override
    public void run(String... args) {
        List<LeBlogArticle> leBlogArticles = Optional.ofNullable(leBlogArticleMapper.selectList(new LambdaQueryWrapper<LeBlogArticle>()
                        .eq(LeBlogArticle::getStatus, ARTICLE_STATUS_PUBLISHED)))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.ARTICLE_NOT_FOUND));

        Map<String, Integer> viewCountMap = leBlogArticles.stream()
                .collect(Collectors.toMap(article -> article.getId().toString(), article -> article.getViewCount().intValue()));

        String key = "article:viewCount";
        redisCache.setCacheMap(key, viewCountMap);
        redisCache.expire(key, 30, TimeUnit.MINUTES);
    }

}
