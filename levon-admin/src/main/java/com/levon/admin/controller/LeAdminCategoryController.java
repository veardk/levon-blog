package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.AdminCategoryCreateValidationDTO;
import com.levon.framework.domain.dto.AdminCategoryUpdateValidationDTO;
import com.levon.framework.service.LeBlogCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/content/category")
public class LeAdminCategoryController {
    @Autowired
    private LeBlogCategoryService leBlogCategoryService;

    /**
     * 获取所有分类列表
     *
     * @return 分类列表结果
     */
    @GetMapping("/listAllCategory")
    @SystemLog("获取所有分类列表")
    public ResponseResult list() {
        return ResponseResult.okResult(leBlogCategoryService.getCateGoryList());
    }


    /**
     * 分页查询分类列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param name     分类名称查询条件
     * @param status   分类状态查询条件
     * @return 分类列表结果
     */
    @GetMapping("/list")
    @SystemLog("分页查询分类列表")
    public ResponseResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String status) {

        return ResponseResult.okResult(leBlogCategoryService.list(pageNum, pageSize, name, status));
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情结果
     */
    @GetMapping("/{id}")
    @SystemLog("获取分类详情")
    public ResponseResult detail(@NotNull(message = "id不能为空") @PathVariable("id") Long id) {
        return ResponseResult.okResult(leBlogCategoryService.detail(id));
    }

    /**
     * 编辑分类
     *
     * @param updateRequest 分类更新请求对象
     * @return 操作结果
     */
    @PutMapping
    @SystemLog("编辑分类")
    public ResponseResult edit(@Validated @RequestBody AdminCategoryUpdateValidationDTO updateRequest) {
        leBlogCategoryService.edit(updateRequest);
        return ResponseResult.okResult();
    }

    /**
     * 新增分类
     *
     * @param createRequest 分类创建请求对象
     * @return 操作结果
     */
    @PostMapping
    @SystemLog("新增分类")
    public ResponseResult add(@Validated @RequestBody AdminCategoryCreateValidationDTO createRequest) {
        leBlogCategoryService.add(createRequest);
        return ResponseResult.okResult();
    }

    /**
     * 删除分类
     *
     * @param ids 分类ID字符串,逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @SystemLog("删除分类")
    public ResponseResult del(@NotNull(message = "id不能为空")
                              @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (idList.contains(0L)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR, "无法删除");
        }

        if (idList.size() == 1) {
            leBlogCategoryService.del(idList.get(0));
        } else {
            leBlogCategoryService.batchDel(idList);
        }
        return ResponseResult.okResult();
    }

    /**
     * 切换分类状态
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @GetMapping("/status/{id}")
    @SystemLog("切换分类状态")
    public ResponseResult changeStatus(@NotNull(message = "id不能为空") @PathVariable Long id) {
        leBlogCategoryService.toggleStatus(id);
        return ResponseResult.okResult();
    }
}
