package com.example.demo.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;




@Entity
@Table(name = "app_version")
@Data
public class AppVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String version;        // 版本号
    
    @Column(nullable = false)
    private Boolean forceUpdate;   // 是否强制更新
    
    @Column(nullable = false)
    private String downloadUrl;    // 下载地址
    
    @Column(length = 1000)
    private String updateContent;  // 更新内容
    
    private Long releaseTime;      // 发布时间
    
    @Column(nullable = false)
    private Boolean isActive;      // 是否是当前活动版本
}