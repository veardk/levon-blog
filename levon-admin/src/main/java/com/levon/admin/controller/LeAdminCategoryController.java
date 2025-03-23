package com.levon.admin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.AdminCategoryCreateValidationDTO;
import com.levon.framework.domain.dto.AdminCategoryUpdateValidationDTO;
import com.levon.framework.domain.vo.AdminExcelCategoryVO;
import com.levon.framework.service.LeBlogCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/content/category")
@Slf4j
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

    /**
     * 导出分类为Excel
     * 
     */
    @GetMapping("/export")
    @SystemLog("导出分类")
    public void export(HttpServletResponse response) {
        try {
            // 设置下载文件的响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("分类数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 获取导出数据
            List<AdminExcelCategoryVO> excelData = leBlogCategoryService.exportData();

            // 写入Excel
            try (OutputStream os = response.getOutputStream()) {
                EasyExcel.write(os, AdminExcelCategoryVO.class)
                        .sheet("分类数据")
                        .doWrite(excelData);
            } catch (IOException e) {
                log.error("Excel写入失败", e);
                writeErrorResponse(response, AppHttpCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
            }
        } catch (SystemException e) {
            log.error("导出分类失败，业务异常：{}", e.getMsg());
            writeErrorResponse(response, e.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error("导出分类失败，系统异常", e);
            writeErrorResponse(response, AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "导出失败：" + e.getMessage());
        }
    }

    /**
     * 导入分类
     *
     * @param file 导入的Excel文件
     * @return 操作结果
     */
    @PostMapping("/import")
    @SystemLog("导入分类")
    public ResponseResult<?> importCategory(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "请选择要导入的Excel文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "请上传Excel文件（.xlsx或.xls格式）");
        }

        try {
            leBlogCategoryService.importCategory(file);
            return ResponseResult.okResult();
        } catch (SystemException e) {
            log.error("导入分类失败，业务异常：{}", e.getMsg());
            return ResponseResult.errorResult(e.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error("导入分类失败，系统异常", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "导入失败：" + e.getMessage());
        }
    }

    /**
     * 写入错误响应（带错误码和消息）
     */
    private void writeErrorResponse(HttpServletResponse response, int code, String message) {
        try {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", code);
            errorResponse.put("msg", message);

            response.getWriter().write(JSON.toJSONString(errorResponse));
        } catch (IOException e) {
            log.error("写入错误响应失败", e);
        }
    }
}