package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "student")
public class Student {
    @Id
    private String studentId;  // 学号作为主键
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String major;
    
    @Column(nullable = false)
    private String grade;
    
    @Column(nullable = false)
    private String password;
    
    @Column
    private String avatar;  // 头像URL
    
    @Column
    private String coverImage;  // 封面图URL
    
    @Column(length = 100)
    private String bio;  // 个人简介
    
    @Column
    private String tags;  // 标签
    
    @Column(nullable = false)
    private Long createTime;
    
    @Column(nullable = false)
    private String role = "USER";  // 默认普通用户
}