package com.example.demo.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private StudentVO userInfo;
}