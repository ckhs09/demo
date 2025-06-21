package com.example.demo.model;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String major;
    private String grade;
    private String oldPassword;
    private String newPassword;
    private String bio;
    private String tags;
}