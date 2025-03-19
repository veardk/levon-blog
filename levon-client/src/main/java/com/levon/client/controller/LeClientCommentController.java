package com.levon.client.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.constants.CommentConstants;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.ClientCommentCreateValidationDTO;
import com.levon.framework.service.LeBlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
//@Api(tags = "评论", description = "评论相关接口")
public class LeClientCommentController {

    @Autowired
    private LeBlogCommentService leBlogCommentService;

    /**
     * 评论列表
     * @param articleId 文章id
     * @param pageNum   当前页码
     * @param pageSize  页码大小
     * @return ResponseResult 包含评论列表的响应结果
     */
    @SystemLog("评论列表")
    @GetMapping("/commentList")
    public ResponseResult linkCommentList(@RequestParam Long articleId,
                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseResult.okResult(leBlogCommentService.commentList(CommentConstants.ARTICLE_COMMENT, articleId, pageNum, pageSize));
    }

    /**
     * 发表评论
     * @param commentValidateDTO 评论DTO
     * @return ResponseResult 操作成功的响应结果
     */
    @SystemLog("发表评论")
    @PostMapping
    public ResponseResult addComment(@Validated @RequestBody ClientCommentCreateValidationDTO commentValidateDTO) {
        leBlogCommentService.addComment(commentValidateDTO);
        return ResponseResult.okResult();
    }


    /**
     * 友链评论列表
     * @param pageNum 当前页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @return ResponseResult 包含友链评论列表的响应结果
     */
    @SystemLog("友链评论列表")
    @GetMapping("/linkCommentList")
    public ResponseResult linkCommentList(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseResult.okResult(leBlogCommentService.commentList(CommentConstants.FRIEND_LINK_COMMENT, null, pageNum, pageSize));
    }


}
