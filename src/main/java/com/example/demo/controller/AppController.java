package com.example.demo.controller;
import com.example.demo.model.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AppVersion;
import com.example.demo.service.AppVersionService;

import lombok.extern.slf4j.Slf4j;
 


@RestController
@RequestMapping("/app")
@Slf4j
public class AppController {
    
    @Autowired
    private AppVersionService appVersionService;
    
    @GetMapping("/version")
    public ApiResponse<?> checkVersion() {
        try {
            AppVersion version = appVersionService.getLatestVersion();
            return ApiResponse.success(version);
        } catch (Exception e) {
            log.error("检查版本失败", e);
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    @PostMapping("/version")
    @PreAuthorize("hasRole('ADMIN')")  // 需要管理员权限
    public ApiResponse<?> createVersion(@RequestBody AppVersion version) {
        try {
            appVersionService.createNewVersion(version);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("创建新版本失败", e);
            return ApiResponse.error(500, e.getMessage());
        }
    }
}