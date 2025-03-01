package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.domain.dto.CommentValidationDTO;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.domain.entry.LeBlogComment;
import com.levon.framework.domain.vo.LeBlogCommentVO;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.mapper.LeBlogArticleMapper;
import com.levon.framework.mapper.SysUserMapper;
import com.levon.framework.service.LeBlogCommentService;
import com.levon.framework.mapper.LeBlogCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author leivik
 * @description 针对表【le_blog_comment(评论表)】的数据库操作Service实现
 * @createDate 2025-02-23 09:14:50
 */
@Service
public class LeBlogCommentServiceImpl extends ServiceImpl<LeBlogCommentMapper, LeBlogComment>
        implements LeBlogCommentService {

    @Autowired
    private LeBlogArticleMapper leBlogArticleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取评论列表
     *
     * @param commentType 文章类型
     * @param articleId   文章id
     * @param pageNum     当前页码
     * @param pageSize    页码大小
     * @return
     */
    @Override
    public PageVO commentList(int commentType, Long articleId, Integer pageNum, Integer pageSize) {
        Page<LeBlogComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LeBlogComment> wrapper = new LambdaQueryWrapper<>();

        if (Objects.nonNull(articleId)) {
            LeBlogArticle leBlogArticle = leBlogArticleMapper.selectById(articleId);
            if (Objects.isNull(leBlogArticle)) {
                throw new RuntimeException("未知文章");
            }
            wrapper.eq(LeBlogComment::getArticleId, articleId);
        }

        wrapper.eq(LeBlogComment::getType, commentType)
                .eq(LeBlogComment::getRootId, -1);

        // 评论按时间排序、按点赞量排序？

        page(page, wrapper);

        if (Objects.isNull(page.getRecords())) {
            throw new RuntimeException("null");
        }

        List<LeBlogCommentVO> leBlogCommentVoList = toCommentVoList(page.getRecords());

        // 添加子评论
        leBlogCommentVoList.forEach(rootCommentVo ->
                rootCommentVo.setChildren(getChildren(commentType, rootCommentVo.getId()))
        );

        return new PageVO(leBlogCommentVoList, page.getTotal());
    }

    /**
     * 转换Vo
     *
     * @param commentList
     * @return
     */
    private List<LeBlogCommentVO> toCommentVoList(List<LeBlogComment> commentList) {

        List<LeBlogCommentVO> commentVoList = BeanCopyUtils.copyBeanList(commentList, LeBlogCommentVO.class);

        commentVoList.stream()
                .forEach(commentVo -> {
                    String nickName = sysUserMapper.selectById(commentVo.getCreateBy()).getNickName();
                    commentVo.setUsername(nickName);

                    if (commentVo.getRootId() != -1) {
                        String toCommentUsername = sysUserMapper.selectById(commentVo.getToCommentUserId()).getNickName();
                        commentVo.setToCommentUsername(toCommentUsername);
                    }
                });

        return commentVoList;
    }

    /**
     * 获取根评论的子评论
     *
     * @param commentType 评论类型
     * @param rootCommentId 根评论id
     * @return
     */
    private List<LeBlogCommentVO> getChildren(int commentType, Long rootCommentId) {
        LambdaQueryWrapper<LeBlogComment> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(LeBlogComment::getRootId, rootCommentId)
                .eq(LeBlogComment::getType, commentType)
                .orderByAsc(LeBlogComment::getCreateTime);

        List<LeBlogCommentVO> childrenVoList = toCommentVoList(list(wrapper));

        return childrenVoList;
    }

    /**
     * 获取评论列表2
     */
//    private List<LeBlogCommentVo> toCommentVoList2(List<LeBlogComment> commentList) {
//
//        List<LeBlogCommentVo> commentVoList = BeanCopyUtils.copyBeanList(commentList, LeBlogCommentVo.class);
//
//        commentVoList.stream()
//                .forEach(commentVo -> {
//                    String nickName = sysUserMapper.selectById(commentVo.getCreateBy()).getNickName();
//                    commentVo.setUsername(nickName);
//
//                    if (commentVo.getRootId() != -1) {
//                        String toCommentUsername = sysUserMapper.selectById(commentVo.getToCommentUserId()).getNickName();
//                        commentVo.setToCommentUsername(toCommentUsername);
//                    }
//                });
//
//        commentVoList.stream()
//                .forEach(commentVo -> {
//                    if(commentVo.getRootId() == -1){
//                        commentVo.setChildren(getChildren(commentVo.getId(), commentVoList));
//                    }else{
//                        commentVo.setChildren(null);
//                    }
//                });
//
//        return commentVoList;
//    }
//
//    private List<LeBlogCommentVo> getChildren(Long commentId, List<LeBlogCommentVo> commentVoList){
//        List<LeBlogCommentVo> childrenVoList = new ArrayList<>();
//
//        commentVoList.stream()
//                .forEach(commentVo ->{
//                    if(commentId == commentVo.getRootId()){
//                        childrenVoList.add(commentVo);
//                    }
//                });
//
//        return childrenVoList;
//    }


    /**
     * 发表评论
     *
     * @param commentValidateDTO
     * @return
     */
    @Override
    public void addComment(CommentValidationDTO commentValidateDTO) {
        LeBlogArticle leBlogArticle = leBlogArticleMapper.selectById(commentValidateDTO.getArticleId());
        if (Objects.isNull(leBlogArticle)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "文章不存在或被删除");
        }

        LeBlogComment leBlogComment = BeanCopyUtils.copyBean(commentValidateDTO, LeBlogComment.class);
        save(leBlogComment);
    }

    /**
     * 删除评论
     */
}




