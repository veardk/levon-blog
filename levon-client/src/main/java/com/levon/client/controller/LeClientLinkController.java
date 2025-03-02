package com.levon.client.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.service.LeBlogLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
public class LeClientLinkController {
    @Autowired
    private LeBlogLinkService leBlogLinkService;

    /**
     * 所有友联列表
     * @return
     */
    @SystemLog("所有友联列表")
    @GetMapping("/getAllLink")
    public ResponseResult getAllLink(){
        return ResponseResult.okResult(leBlogLinkService.getAllLink());
    }
}
