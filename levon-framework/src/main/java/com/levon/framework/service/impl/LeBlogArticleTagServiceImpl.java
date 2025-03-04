package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.domain.entry.LeBlogArticleTag;
import com.levon.framework.service.LeBlogArticleTagService;
import com.levon.framework.mapper.LeBlogArticleTagMapper;
import org.springframework.stereotype.Service;

/**
* @author leivik
* @description 针对表【le_blog_article_tag(文章标签关联表)】的数据库操作Service实现
* @createDate 2025-03-02 21:33:28
*/
@Service
public class LeBlogArticleTagServiceImpl extends ServiceImpl<LeBlogArticleTagMapper, LeBlogArticleTag>
    implements LeBlogArticleTagService{

}




