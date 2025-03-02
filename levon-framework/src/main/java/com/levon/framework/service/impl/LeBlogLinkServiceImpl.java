package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.constants.LinkConstants;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.domain.entry.LeBlogLink;
import com.levon.framework.domain.vo.ClientLinkVO;
import com.levon.framework.service.LeBlogLinkService;
import com.levon.framework.mapper.LeBlogLinkMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
* @author leivik
* @description 针对表【le_blog_link(友链)】的数据库操作Service实现
* @createDate 2025-02-20 22:55:44
*/
@Service
public class LeBlogLinkServiceImpl extends ServiceImpl<LeBlogLinkMapper, LeBlogLink>
    implements LeBlogLinkService{

    /**
     * 获取所有友联列表
     * @return
     */
    @Override
    public List<ClientLinkVO> getAllLink() {
        LambdaQueryWrapper<LeBlogLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeBlogLink::getStatus, LinkConstants.AUDIT_STATUS_APPROVED);

        List<LeBlogLink> list = list(wrapper);

        if (list != null || !list.isEmpty()) {
            List<ClientLinkVO> leBlogLinkVoList = BeanCopyUtils.copyBeanList(list, ClientLinkVO.class);
            return leBlogLinkVoList;
        }
        return Collections.emptyList();
    }
}




