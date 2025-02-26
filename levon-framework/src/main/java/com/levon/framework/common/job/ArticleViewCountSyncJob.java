package com.levon.framework.common.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.mapper.LeBlogArticleMapper;
import com.levon.framework.service.LeBlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ArticleViewCountSyncJob {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    LeBlogArticleService leBlogArticleService;

    /**
     * 定时任务：redis->mysql
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncArticleViewCountFromRedisToMySQL() {
        String redisKey = "article:viewCount";
        Map<String, Integer> cacheMap = redisCache.getCacheMap(redisKey);

        List<LeBlogArticle> articles = cacheMap
                .entrySet()
                .stream()
                .map(entry -> {
                    return new LeBlogArticle(Long.valueOf(entry.getKey()), entry.getValue().longValue());
                }).collect(Collectors.toList());

        boolean result = leBlogArticleService.updateBatchById(articles);
        if (!result) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "Error: ArticleViewCount transfer From Redis To MySQL ");
        }
    }
}
