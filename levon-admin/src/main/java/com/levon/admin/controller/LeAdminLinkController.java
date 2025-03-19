package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.AdminLinkCreateValidationDTO;
import com.levon.framework.domain.dto.AdminLinkUpdateValidationDTO;
import com.levon.framework.domain.dto.ChangeLinkStatusValidationDTO;
import com.levon.framework.service.LeBlogLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 友情链接管理控制器
 */
@RestController
@RequestMapping("/content/link")
public class LeAdminLinkController {

    @Autowired
    private LeBlogLinkService leBlogLinkService;

    /**
     * 分页查询友情链接
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param name     友情链接名称查询条件
     * @param status   友情链接状态查询条件
     * @return 友情链接列表结果
     */
    @GetMapping("/list")
    @SystemLog("分页查询友情链接")
    public ResponseResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String status) {
        return ResponseResult.okResult(leBlogLinkService.pageList(pageNum, pageSize, name, status));
    }

    /**
     * 获取友情链接详情
     *
     * @param id 友情链接ID
     * @return 友情链接详情结果
     */
    @GetMapping("/{id}")
    @SystemLog("获取友情链接详情")
    public ResponseResult detail(@NotNull(message = "id不能为空") @PathVariable Long id) {
        return ResponseResult.okResult(leBlogLinkService.detail(id));
    }

    /**
     * 编辑友情链接
     *
     * @param updateRequest 友情链接更新请求对象
     * @return 操作结果
     */
    @PutMapping
    @SystemLog("编辑友情链接")
    public ResponseResult edit(@Validated @RequestBody AdminLinkUpdateValidationDTO updateRequest) {
        leBlogLinkService.edit(updateRequest);
        return ResponseResult.okResult();
    }

    /**
     * 新增友情链接
     *
     * @param createRequest 友情链接创建请求对象
     * @return 操作结果
     */
    @PostMapping
    @SystemLog("新增友情链接")
    public ResponseResult add(@Validated @RequestBody AdminLinkCreateValidationDTO createRequest) {
        leBlogLinkService.add(createRequest);
        return ResponseResult.okResult();
    }

    /**
     * 删除友情链接
     *
     * @param ids 友情链接ID字符串,逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @SystemLog("删除友情链接")
    public ResponseResult del(@NotNull(message = "ids不能为空") @PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (idList.size() == 1) {
            leBlogLinkService.del(idList.get(0));
        } else {
            leBlogLinkService.baechDel(idList);
        }
        return ResponseResult.okResult();
    }

    /**
     * 切换友情链接状态
     *
     * @param changeRequest 友情链接状态变更请求对象
     * @return 操作结果
     */
    @PutMapping("/changeLinkStatus")
    @SystemLog("切换友情链接状态")
    public ResponseResult changeStatus(@Validated @RequestBody ChangeLinkStatusValidationDTO changeRequest) {
        leBlogLinkService.changeStatus(changeRequest);
        return ResponseResult.okResult();
    }
}
