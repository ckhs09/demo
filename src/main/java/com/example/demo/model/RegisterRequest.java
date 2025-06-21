package com.example.demo.model;
import lombok.Data;
@Data
public class RegisterRequest {
   private String studentId;
   private String password;
   private String name;
   private String major;
   private String grade;
   private String avatar;
}