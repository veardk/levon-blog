package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.entry.LeBlogTag;
import com.levon.framework.service.LeBlogTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/tag")
public class LeBlogTagController {

    @Autowired
    private LeBlogTagService leBlogTagService;

    @GetMapping("/list")
    @SystemLog
    public ResponseResult getTagList(){
        return ResponseResult.okResult(leBlogTagService.list());
    }
}
