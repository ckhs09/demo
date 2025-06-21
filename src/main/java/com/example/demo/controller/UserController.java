package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.UpdateUserRequest;
import com.example.demo.service.AuthService;
import com.example.demo.model.ApiResponse;
import com.example.demo.util.JwtUtil;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/update")
    public ApiResponse<?> updateUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateUserRequest request) {
        try {
            String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
            authService.updateUser(studentId, request);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}