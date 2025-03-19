package com.levon.framework.common.job;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.RedisCache;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.service.LeBlogArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j // 使用 Lombok 的 Slf4j 注解，简化日志记录
public class ArticleViewCountSyncJob {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LeBlogArticleService leBlogArticleService;

    private static final String REDIS_KEY = "article:viewCount";
    private static final int EXPIRE_TIME = 30; // 过期时间 30 分钟
    private static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.MINUTES;

    /**
     * 定时任务：redis->mysql
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncArticleViewCountFromRedisToMySQL() {
        try {
            Map<String, Integer> cacheMap = redisCache.getCacheMap(REDIS_KEY);

            if (CollectionUtils.isEmpty(cacheMap)) {
                log.warn("No article view count data found in Redis.");
                return;
            }

            List<LeBlogArticle> articles = cacheMap.entrySet().stream()
                    .map(entry -> new LeBlogArticle(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
                    .collect(Collectors.toList());

            boolean result = leBlogArticleService.updateBatchById(articles);

            if (!result) {
                throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "Error: ArticleViewCount transfer From Redis To MySQL ");
            }

            // 刷新 Redis 过期时间
            redisCache.expire(REDIS_KEY, EXPIRE_TIME, EXPIRE_TIME_UNIT);
            log.info("Article view count synchronized from Redis to MySQL successfully. Refreshed Redis key {} with expire time {} {}", REDIS_KEY, EXPIRE_TIME, EXPIRE_TIME_UNIT);

        } catch (Exception e) {
            log.error("Error occurred while synchronizing article view count from Redis to MySQL: ", e);
        }
    }
}
