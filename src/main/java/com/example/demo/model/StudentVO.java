package com.example.demo.model;

import lombok.Data;

@Data
public class StudentVO {
    private String studentId;
    private String name;
    private String major;
    private String grade;
    private String avatar;
    private String coverImage;
    private String bio;
    private String tags;
}