package com.levon.admin.controller;

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

@RestController
@RequestMapping("/content/category")
public class LeAdminCategoryController {
    @Autowired
    private LeBlogCategoryService leBlogCategoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult list(){
        return ResponseResult.okResult(leBlogCategoryService.getCateGoryList());
    }


    @GetMapping("/list")
    public ResponseResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) String status){

        return ResponseResult.okResult(leBlogCategoryService.list(pageNum, pageSize, name, status));
    }

    @GetMapping("{id}")
    public ResponseResult detail(@PathVariable("id") Long id){
        return ResponseResult.okResult(leBlogCategoryService.detail(id));
    }

    @PutMapping
    public ResponseResult edit(@Validated @RequestBody AdminCategoryUpdateValidationDTO updateRequest){
        leBlogCategoryService.update(updateRequest);
        return ResponseResult.okResult();
    }

    @PostMapping
    public ResponseResult add(@Validated @RequestBody AdminCategoryCreateValidationDTO createRequest){
        leBlogCategoryService.add(createRequest);
        return ResponseResult.okResult();
    }

    @DeleteMapping ("/{ids}")
    public ResponseResult del(@NotNull(message = "id不能为空") @PathVariable String ids){
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        if(idList.size() == 1){
            leBlogCategoryService.del(idList.get(0));
        }else {
            leBlogCategoryService.delIds(idList);
        }
        return ResponseResult.okResult();
    }

    @GetMapping("/status/{id}")
    public ResponseResult changeStatus(@NotNull(message = "id不能为空") @PathVariable Long id){
        leBlogCategoryService.changeStatus(id);
        return ResponseResult.okResult();
    }
}
