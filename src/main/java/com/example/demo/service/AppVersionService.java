package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import com.example.demo.entity.AppVersion;
import com.example.demo.repository.AppVersionRepository;

@Service
@Slf4j
public class AppVersionService {
    
    @Autowired
    private AppVersionRepository appVersionRepository;
    
    public AppVersion getLatestVersion() {
        return appVersionRepository.findFirstByIsActiveTrueOrderByVersionDesc()
            .orElseThrow(() -> new RuntimeException("未找到版本信息"));
    }
    
    @Transactional
    public void createNewVersion(AppVersion version) {
        // 停用其他版本
        appVersionRepository.findAll().forEach(v -> {
            v.setIsActive(false);
            appVersionRepository.save(v);
        });
        
        // 设置新版本
        version.setIsActive(true);
        version.setReleaseTime(System.currentTimeMillis());
        appVersionRepository.save(version);
        
        log.info("新版本发布成功：{}", version.getVersion());
    }
}