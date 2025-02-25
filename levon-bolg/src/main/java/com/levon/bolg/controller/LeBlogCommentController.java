package com.levon.bolg.controller;

import com.levon.framework.common.constants.CommentConstants;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.CommentValidationDTO;
import com.levon.framework.service.LeBlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class LeBlogCommentController {

    @Autowired
    private LeBlogCommentService leBlogCommentService;

    /**
     * 获取评论列表
     *
     * @param articleId 文章id
     * @param pageNum   当前页码
     * @param pageSize  页码大小
     * @return
     */
    @GetMapping("/commentList")
    public ResponseResult commentList(@RequestParam Long articleId,
                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseResult.okResult(leBlogCommentService.commentList(CommentConstants.ARTICLE_COMMENT, articleId, pageNum, pageSize));
    }

    /**
     * 发表评论
     *
     * @param commentValidateDTO
     * @return
     */
    @PostMapping
    public ResponseResult addComment(@Validated @RequestBody CommentValidationDTO commentValidateDTO) {
        leBlogCommentService.addComment(commentValidateDTO);
        return ResponseResult.okResult();
    }

    @GetMapping("/linkCommentList")
    public ResponseResult commentList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseResult.okResult(leBlogCommentService.commentList(CommentConstants.FRIEND_LINK_COMMENT, null, pageNum, pageSize));
    }


}
