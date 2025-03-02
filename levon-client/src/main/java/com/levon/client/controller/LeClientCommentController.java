package com.levon.client.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.constants.CommentConstants;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.ClientCommentCreateValidationDTO;
import com.levon.framework.service.LeBlogCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论", description = "评论相关接口")
public class LeClientCommentController {

    @Autowired
    private LeBlogCommentService leBlogCommentService;

    /**
     * 评论列表
     *
     * @param articleId 文章id
     * @param pageNum   当前页码
     * @param pageSize  页码大小
     * @return
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
     *
     * @param commentValidateDTO
     * @return
     */
    @SystemLog("发表评论")
    @PostMapping
    public ResponseResult addComment(@Validated @RequestBody ClientCommentCreateValidationDTO commentValidateDTO) {
        leBlogCommentService.addComment(commentValidateDTO);
        return ResponseResult.okResult();
    }


    /**
     * 友链评论列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @SystemLog("友链评论列表")
    @GetMapping("/linkCommentList")
    @ApiOperation(value = "友链评论列表", notes = "获取一页友链评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页号"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小")
    })
    public ResponseResult linkCommentList(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseResult.okResult(leBlogCommentService.commentList(CommentConstants.FRIEND_LINK_COMMENT, null, pageNum, pageSize));
    }


}
