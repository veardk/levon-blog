package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.common.util.SecurityUtils;
import com.levon.framework.domain.dto.ClientCommentCreateValidationDTO;
import com.levon.framework.domain.entry.LeBlogArticle;
import com.levon.framework.domain.entry.LeBlogComment;
import com.levon.framework.domain.vo.ClientCommentVO;
import com.levon.framework.domain.vo.PageVO;
import com.levon.framework.mapper.LeBlogArticleMapper;
import com.levon.framework.mapper.LeBlogCommentMapper;
import com.levon.framework.mapper.SysUserMapper;
import com.levon.framework.service.LeBlogCommentService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.levon.framework.common.constants.CommentConstants.FRIEND_LINK_COMMENT_ARTICLE_ID;

/**
 * @author leivik
 * @description 针对表【le_blog_comment(评论表)】的数据库操作Service实现
 * @createDate 2025-02-23 09:14:50
 */
@Slf4j
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
     * @return PageVO 评论列表响应
     */
    @Override
    public PageVO commentList(int commentType, Long articleId, Integer pageNum, Integer pageSize) {
        Page<LeBlogComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LeBlogComment> wrapper = new LambdaQueryWrapper<>();

        // articleId不为空
        if (Objects.nonNull(articleId)) {
            Optional.ofNullable(leBlogArticleMapper.selectById(articleId))
                    .orElseThrow(() -> new SystemException(AppHttpCodeEnum.ARTICLE_NOT_FOUND, "Failed to found article with id:  " + articleId));
            wrapper.eq(LeBlogComment::getArticleId, articleId);
        }

        // 获取对应类型的根评论
        wrapper.eq(LeBlogComment::getType, commentType)
                .eq(LeBlogComment::getRootId, -1);

        // 未实现功能：评论按时间排序、按点赞量排序?

        page(page, wrapper);

        if (page.getRecords() == null) {
            return new PageVO(Collections.emptyList(), 0L);
        }

        List<ClientCommentVO> commentVoListTree = buildCommentTree(commentType, page.getRecords());

        return new PageVO(commentVoListTree, page.getTotal());
    }

    /**
     * 构建评论树
     *
     * @param commentType 评论类型
     * @param commentList 评论列表
     * @return List<ClientCommentVO>
     */
    @NotNull
    private List<ClientCommentVO> buildCommentTree(int commentType, List<LeBlogComment> commentList) {
        List<ClientCommentVO> rootCommentVoList = toCommentVoList(commentList);

        // 获取当前页根评论的 ID 列表
        List<Long> rootCommentIds = rootCommentVoList.stream()
                .map(ClientCommentVO::getId)
                .toList();

        // 批量查询子评论
        LambdaQueryWrapper<LeBlogComment> childrenWrapper = new LambdaQueryWrapper<>();
        childrenWrapper.eq(LeBlogComment::getType, commentType)
                .in(LeBlogComment::getRootId, rootCommentIds);
        List<LeBlogComment> childrenCommentList = list(childrenWrapper);
        List<ClientCommentVO> childrenCommentVoList = toCommentVoList(childrenCommentList);

        // 构建当前页根评论及其子评论的树
        Map<Long, ClientCommentVO> commentVOMap = new HashMap<>();
        rootCommentVoList.forEach(commentVO -> {
            commentVO.setChildren(new ArrayList<>());
            commentVOMap.put(commentVO.getId(), commentVO);
        });
        childrenCommentVoList.forEach(childCommentVO -> {
            ClientCommentVO parent = commentVOMap.get(childCommentVO.getRootId());
            if (parent != null) {
                parent.getChildren().add(childCommentVO);
            }
        });

        return rootCommentVoList;
    }

    /**
     * 转换Vo
     *
     * @param commentList 评论列表
     * @return List<ClientCommentVO>
     */
    private List<ClientCommentVO> toCommentVoList(List<LeBlogComment> commentList) {

        List<ClientCommentVO> commentVoList = BeanCopyUtils.copyBeanList(commentList, ClientCommentVO.class);

        commentVoList.forEach(commentVo -> {
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
     * 发表评论
     *
     * @param commentValidateDTO 评论DTO
     */
    @Override
    public void addComment(ClientCommentCreateValidationDTO commentValidateDTO) {
        log.info("Adding comment. commentValidateDTO: {}", commentValidateDTO);
        LeBlogArticle leBlogArticle = leBlogArticleMapper.selectById(commentValidateDTO.getArticleId());
        if (Objects.isNull(leBlogArticle) && !commentValidateDTO.getArticleId().equals(FRIEND_LINK_COMMENT_ARTICLE_ID)) {
            log.error("Article not found or deleted. articleId: {}", commentValidateDTO.getArticleId());
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "文章不存在或被删除");
        }

        LeBlogComment leBlogComment = BeanCopyUtils.copyBean(commentValidateDTO, LeBlogComment.class);
        if (!save(leBlogComment)) {
            log.error("Failed to save comment: {}", leBlogComment);
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "评论保存失败");
        }
        log.info("Comment added successfully: {}", leBlogComment);
    }

    /**
     * 删除评论
     * <p>
     * 只能删除自己的评论
     */
    @Override
    public void deleteComment(Long commentId) {
        // 评论是否存在
        Optional.ofNullable(getById(commentId))
                .orElseThrow(() -> new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "评论不存在"));

        // 评论是否是自己的
        if (!getById(commentId).getCreateBy().equals(SecurityUtils.getUserId())) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "评论不是自己的");
        }

        // 评论是否是根评论
        if (getById(commentId).getRootId() != -1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "评论不是根评论");
        }

        // 评论是否是友链评论
        if (getById(commentId).getArticleId().equals(FRIEND_LINK_COMMENT_ARTICLE_ID)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "友链评论不能删除");
        }

        // 删除评论
        if (!removeById(commentId)) {
            log.error("Failed to delete comment: {}", commentId);
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "评论删除失败");
        }

        log.info("Comment deleted successfully: {}", commentId);
    }

}