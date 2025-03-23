package com.levon.framework.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.domain.dto.AdminExcelCategoryDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelCategoryListener extends AnalysisEventListener<AdminExcelCategoryDTO> {

    private final List<AdminExcelCategoryDTO> excelCategoryDTOList = new ArrayList<>();
    private int rowIndex = 0;

    @Override
    public void invoke(AdminExcelCategoryDTO data, AnalysisContext context) {
        rowIndex++;
        log.info("解析第{}行数据：{}", rowIndex, JSON.toJSONString(data));
        
        // 只做基础数据验证
        validateData(data);
        excelCategoryDTOList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成，共解析 {} 条数据", rowIndex);
    }

    private void validateData(AdminExcelCategoryDTO data) {
        if (data == null) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR,
                String.format("第%d行数据为空", rowIndex));
        }

        // 验证分类名称
        if (StringUtils.isBlank(data.getName())) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR,
                String.format("第%d行分类名称不能为空", rowIndex));
        }
        if (data.getName().length() > 30) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR,
                String.format("第%d行分类名称长度不能超过30个字符", rowIndex));
        }

        // 验证分类描述
        if (StringUtils.isNotBlank(data.getDescription()) && data.getDescription().length() > 100) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR,
                String.format("第%d行分类描述长度不能超过100个字符", rowIndex));
        }

        // 验证状态值
        String status = data.getStatus();
        if (StringUtils.isBlank(status)) {
            data.setStatus("0");
        } else if (!"0".equals(status) && !"1".equals(status)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR,
                String.format("第%d行状态值必须是0或1", rowIndex));
        }
    }

    /**
     * 获取解析后的数据
     */
    public List<AdminExcelCategoryDTO> getData() {
        return excelCategoryDTOList;
    }
}