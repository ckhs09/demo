package com.example.demo.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String studentId;
    private String password;
}