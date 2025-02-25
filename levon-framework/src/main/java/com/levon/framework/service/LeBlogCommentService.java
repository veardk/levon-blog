package com.levon.framework.service;

import com.levon.framework.domain.dto.CommentValidationDTO;
import com.levon.framework.domain.entry.LeBlogComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.PageVO;

/**
* @author leivik
* @description 针对表【le_blog_comment(评论表)】的数据库操作Service
* @createDate 2025-02-23 09:14:50
*/
public interface LeBlogCommentService extends IService<LeBlogComment> {

    /**
     * 获取评论列表
     *
     * @param commentType 文章类型
     * @param articleId   文章id
     * @param pageNum     当前页码
     * @param pageSize    页码大小
     * @return
     */
    PageVO commentList(int commentType, Long articleId, Integer pageNum, Integer pageSize);

    /**
     * 发表评论
     * @param commentValidateDTO
     * @return
     */
    void addComment(CommentValidationDTO commentValidateDTO);
}
