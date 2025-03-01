package com.levon.framework.mapper;

import com.levon.framework.domain.entry.LeBlogTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author leivik
* @description 针对表【le_blog_tag(标签)】的数据库操作Mapper
* @createDate 2025-02-28 14:03:47
* @Entity com.levon.framework.domain.entry.LeBlogTag
*/
@Mapper
public interface LeBlogTagMapper extends BaseMapper<LeBlogTag> {

}




