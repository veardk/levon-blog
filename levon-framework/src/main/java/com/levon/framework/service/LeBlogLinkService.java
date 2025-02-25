package com.levon.framework.service;

import com.levon.framework.domain.entry.LeBlogLink;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.LeBlogLinkVO;

import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_link(友链)】的数据库操作Service
* @createDate 2025-02-20 22:55:44
*/
public interface LeBlogLinkService extends IService<LeBlogLink> {
    /**
     * 获取所有友联列表
     * @return
     */
    List<LeBlogLinkVO> getAllLink();
}
