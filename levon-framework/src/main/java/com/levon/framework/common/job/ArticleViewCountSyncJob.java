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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ArticleViewCountSyncJob {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LeBlogArticleService leBlogArticleService;

    private static final String REDIS_KEY = "article:viewCount";
    private static final int EXPIRE_TIME = 24; // 过期时间改为24小时
    private static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.HOURS;
    private static final String SYNC_LOCK_KEY = "lock:article:view:sync";
    private static final int BATCH_SIZE = 100; // 批处理大小

    /**
     * 定时任务,每10分钟同步一次: redis->mysql
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void syncArticleViewCountFromRedisToMySQL() {
        // 获取分布式锁，防止多实例同时执行
        boolean acquired = redisCache.tryLock(SYNC_LOCK_KEY, 300); // 300秒锁
        if (!acquired) {
            log.info("其他实例正在执行同步任务，本次同步跳过");
            return;
        }
        
        try {
            log.info("开始同步文章浏览量数据到数据库");
            Map<String, Integer> cacheMap = redisCache.getCacheMap(REDIS_KEY);

            if (CollectionUtils.isEmpty(cacheMap)) {
                log.info("Redis中没有需要同步的文章浏览量数据");
                return;
            }

            // 分批处理文章浏览量数据
            List<LeBlogArticle> batch = new ArrayList<>();
            int count = 0;
            
            for (Map.Entry<String, Integer> entry : cacheMap.entrySet()) {
                Long articleId = Long.valueOf(entry.getKey());
                Integer viewCount = entry.getValue();
                
                LeBlogArticle article = new LeBlogArticle();
                article.setId(articleId);
                article.setViewCount(viewCount.longValue());
                batch.add(article);
                
                // 达到批处理阈值，执行批量更新
                if (batch.size() >= BATCH_SIZE) {
                    updateBatch(batch);
                    count += batch.size();
                    batch.clear();
                }
            }
            
            // 处理剩余数据
            if (!batch.isEmpty()) {
                updateBatch(batch);
                count += batch.size();
            }

            // 刷新 Redis 过期时间
            redisCache.expire(REDIS_KEY, EXPIRE_TIME, EXPIRE_TIME_UNIT);
            log.info("文章浏览量同步完成，共同步{}条记录，刷新Redis数据过期时间：{}小时", count, EXPIRE_TIME);

        } catch (Exception e) {
            log.error("同步文章浏览量失败", e);
        } finally {
            // 释放锁
            redisCache.releaseLock(SYNC_LOCK_KEY);
        }
    }
    
    /**
     * 批量更新文章浏览量
     */
    private void updateBatch(List<LeBlogArticle> articles) {
        try {
            boolean result = leBlogArticleService.updateBatchById(articles);
            if (!result) {
                throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "批量更新文章浏览量失败");
            }
            log.info("批量更新{}篇文章浏览量成功", articles.size());
        } catch (Exception e) {
            log.error("批量更新文章浏览量异常: {}", e.getMessage(), e);
        }
    }
}
