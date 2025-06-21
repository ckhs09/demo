package com.example.demo.model;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    
    public static ApiResponse<Void> error(int code, String message) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}