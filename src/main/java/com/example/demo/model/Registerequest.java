package com.example.demo.model;

import lombok.Data;

@Data
public class Registerequest {
   private String studentId;
   private String password;
   private String name;
   private String major;
   private String grade;
   private String avatar;  // 添加这行
}