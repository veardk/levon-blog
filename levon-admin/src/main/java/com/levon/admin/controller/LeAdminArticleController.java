package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.AdminArticleCreateValidationDTO;
import com.levon.framework.domain.dto.AdminArticleUpdateValidationDTO;
import com.levon.framework.service.LeBlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章管理
 */
@RestController
@RequestMapping("/content/article")
public class LeAdminArticleController {

    @Autowired
    private LeBlogArticleService leBlogArticleService;

    /**
     * 新增文章
     *
     * @param createRequest 文章创建请求对象
     * @return 响应结果
     */
    @PostMapping
    @SystemLog("新增文章")
    public ResponseResult add(@Validated @RequestBody AdminArticleCreateValidationDTO createRequest) {
        leBlogArticleService.add(createRequest);
        return ResponseResult.okResult();
    }

    /**
     * 查询文章列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param title    文章标题查询条件
     * @param summary  文章摘要查询条件
     * @return 响应结果
     */
    @GetMapping("/list")
    @SystemLog("查询文章列表")
    public ResponseResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) String title,
                               @RequestParam(required = false) String summary) {
        return ResponseResult.okResult(leBlogArticleService.getList(pageNum, pageSize, title, summary));
    }

    /**
     * 查询文章详情
     *
     * @param id 文章ID
     * @return 响应结果
     */
    @GetMapping("/{id}")
    @SystemLog("查询文章详情")
    public ResponseResult detail(@NotNull(message = "文章ID不能为空") @PathVariable("id") Long id) {
        return ResponseResult.okResult(leBlogArticleService.detail(id));
    }

    /**
     * 编辑文章
     *
     * @param updateRequest 文章更新请求对象
     * @return 响应结果
     */
    @PutMapping
    @SystemLog("编辑文章")
    public ResponseResult edit(@Validated @RequestBody AdminArticleUpdateValidationDTO updateRequest) {
        leBlogArticleService.edit(updateRequest);
        return ResponseResult.okResult();
    }

    /**
     * 删除文章
     *
     * @param ids 文章ID列表(以逗号分隔)
     * @return 响应结果
     */
    @DeleteMapping("/{ids}")
    @SystemLog("删除文章")
    public ResponseResult del(@NotNull(message = "文章ID不能为空") @PathVariable("ids") String ids) {
        List<Long> delIdList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (delIdList.size() == 1) {
            leBlogArticleService.del(delIdList.get(0));
        } else {
            // 批量删除
            leBlogArticleService.batchDel(delIdList);
        }
        return ResponseResult.okResult();
    }

}
