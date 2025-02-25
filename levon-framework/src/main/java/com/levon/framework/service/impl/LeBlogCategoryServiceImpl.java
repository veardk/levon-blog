package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.domain.entry.LeBlogCategory;
import com.levon.framework.domain.vo.LeBlogCategoryVO;
import com.levon.framework.mapper.LeBlogCategoryMapper;
import com.levon.framework.service.LeBlogCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_category(分类表)】的数据库操作Service实现
* @createDate 2025-02-20 22:53:41
*/
@Service
public class LeBlogCategoryServiceImpl extends ServiceImpl<LeBlogCategoryMapper, LeBlogCategory>
    implements LeBlogCategoryService {

    @Autowired
    private LeBlogCategoryMapper leBlogCategoryMapper;

    /**
     * 获取文章分类列表
     * @return
     */
    @Override
    public List<LeBlogCategoryVO> getCateGoryList() {
        List<LeBlogCategoryVO> leBlogCategoryVos = BeanCopyUtils.copyBeanList(leBlogCategoryMapper.getCategoryList(), LeBlogCategoryVO.class);
        return leBlogCategoryVos;
    }
}




