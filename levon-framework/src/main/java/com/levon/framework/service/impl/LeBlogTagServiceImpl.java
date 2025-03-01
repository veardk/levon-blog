package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.domain.entry.LeBlogTag;
import com.levon.framework.service.LeBlogTagService;
import com.levon.framework.mapper.LeBlogTagMapper;
import org.springframework.stereotype.Service;

/**
* @author leivik
* @description 针对表【le_blog_tag(标签)】的数据库操作Service实现
* @createDate 2025-02-28 14:03:47
*/
@Service
public class LeBlogTagServiceImpl extends ServiceImpl<LeBlogTagMapper, LeBlogTag>
    implements LeBlogTagService{

}




