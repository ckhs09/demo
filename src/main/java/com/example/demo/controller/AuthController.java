package com.example.demo.controller;
import com.example.demo.entity.Student;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.RegisterRequest;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.util.JwtUtil;
@Slf4j
@RestController
@RequestMapping("/")

public class AuthController {
   
   @Autowired
   private AuthService authService;
   
   @Autowired
   private JwtUtil jwtUtil;
   
   @PostMapping("/register")
   public ApiResponse<?> register(
      @RequestParam("studentId") String studentId,
      @RequestParam("password") String password,
      @RequestParam("name") String name,
      @RequestParam("major") String major,
      @RequestParam("grade") String grade,
      @RequestParam(value = "file", required = false) MultipartFile file  // 头像可选
    ) {
      String requestId = UUID.randomUUID().toString().substring(0, 8);
      log.info("[{}] >>> 开始处理注册请求 - studentId: {} <<<", requestId, studentId);
      
      try {
          RegisterRequest request = new RegisterRequest();
          request.setStudentId(studentId);
          request.setPassword(password);
          request.setName(name);
          request.setMajor(major);
          request.setGrade(grade);
          
          // 处理头像
          String avatarUrl = null;
          if (file != null && !file.isEmpty()) {
              avatarUrl = authService.handleAvatar(file);
              request.setAvatar(avatarUrl);
          }
          
          authService.register(request);
          log.info("[{}] >>> 注册成功 <<<", requestId);
          return ApiResponse.success(null);
      } catch (Exception e) {
          log.error("[{}] !!! 注册失败: {} <<<", requestId, e.getMessage());
          return ApiResponse.error(500, e.getMessage());
      
      }
  }
   
   
   @PostMapping("/login")
   public ApiResponse<?> login(@RequestBody LoginRequest request) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始处理登录请求 - studentId: {} <<<", requestId, request.getStudentId());
       
       try {
           LoginResponse response = authService.login(request);
           log.info("[{}] >>> 登录成功 <<<", requestId);
           return ApiResponse.success(response);
       } catch (Exception e) {
           log.error("[{}] !!! 登录失败: {} <<<", requestId, e.getMessage());
           return ApiResponse.error(403, e.getMessage());
       }
   }
   
   @PostMapping("/user/avatar")
   public ApiResponse<?> updateAvatar(
           @RequestHeader("Authorization") String token,
           @RequestParam("file") MultipartFile file) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始更新用户头像 <<<", requestId);
       
       try {
           String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
           String avatarUrl = authService.updateAvatar(studentId, file);
           return ApiResponse.success(Collections.singletonMap("avatar", avatarUrl));
       } catch (Exception e) {
           log.error("[{}] 更新头像失败: {}", requestId, e.getMessage(), e);
           return ApiResponse.error(500, e.getMessage());
       }
   }
   
   @PostMapping("/user/cover")
   public ApiResponse<?> updateCoverImage(
           @RequestHeader("Authorization") String token,
           @RequestParam("file") MultipartFile file) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始更新用户封面图 <<<", requestId);
       
       try {
           String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
           String coverUrl = authService.updateCoverImage(studentId, file);
           return ApiResponse.success(Collections.singletonMap("coverImage", coverUrl));
       } catch (Exception e) {
           log.error("[{}] 更新封面图失败: {}", requestId, e.getMessage(), e);
           return ApiResponse.error(500, e.getMessage());
       }
   }
   
   @GetMapping("/user/info")
   public ApiResponse<?> getUserInfo(@RequestHeader("Authorization") String token) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始获取用户信息 <<<", requestId);
       
       try {
           String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
           return ApiResponse.success(authService.getUserInfo(studentId));
       } catch (Exception e) {
           log.error("[{}] 获取用户信息失败: {}", requestId, e.getMessage(), e);
           return ApiResponse.error(500, e.getMessage());
       }
   }
}
