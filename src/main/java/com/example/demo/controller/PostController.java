package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.CreatePostRequest;
import com.example.demo.service.PostService;
import com.example.demo.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import com.example.demo.model.CreateCommentRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/posts")
@CrossOrigin
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public ApiResponse<?> createPost(@RequestHeader("Authorization") String token,
                                   @RequestBody CreatePostRequest request) {
        try {
            // 从token中获取学号
            String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
            postService.createPost(studentId, request);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
    @GetMapping
    public ApiResponse<?> getPosts(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String studentId = null;
            if (token != null && token.startsWith("Bearer ")) {
                studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
            }
            Map<String, Object> result = postService.getPosts(type, page, pageSize, sortBy, studentId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    @PostMapping("/{postId}/like")
    public ApiResponse<?> likePost(@PathVariable Long postId,
                                 @RequestHeader("Authorization") String token) {
        try {
            String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
            Map<String, Object> result = postService.toggleLike(postId, studentId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
    @GetMapping("/{postId}")
public ApiResponse<?> getPostDetail(
        @PathVariable Long postId,
        @RequestHeader(value = "Authorization", required = false) String token) {
    try {
        String studentId = null;
        if (token != null && token.startsWith("Bearer ")) {
            studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
        }
        Map<String, Object> result = postService.getPostDetail(postId, studentId);
        return ApiResponse.success(result);
    } catch (Exception e) {
        return ApiResponse.error(500, e.getMessage());
    }
}
@GetMapping("/{postId}/comments")
public ApiResponse<?> getComments(@PathVariable Long postId) {
    try {
        List<Map<String, Object>> comments = postService.getComments(postId);
        return ApiResponse.success(comments);
    } catch (Exception e) {
        return ApiResponse.error(500, e.getMessage());
    }
}

@PostMapping("/{postId}/comments")
public ApiResponse<?> createComment(
        @PathVariable Long postId,
        @RequestHeader("Authorization") String token,
        @RequestBody CreateCommentRequest request) {
    try {
        String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
        postService.createComment(postId, studentId, request);
        return ApiResponse.success(null);
    } catch (Exception e) {
        return ApiResponse.error(500, e.getMessage());
    }
}
@GetMapping("/user/posts")
public ApiResponse<?> getUserPosts(
        @RequestParam(required = false) String type,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestHeader("Authorization") String token) {
        
    String requestId = UUID.randomUUID().toString().substring(0, 8);
    log.info("[{}] >>> 开始处理获取用户帖子请求 <<<", requestId);
    log.info("[{}] 请求参数: type={}, page={}, pageSize={}", requestId, type, page, pageSize);
    log.info("[{}] Token: {}", requestId, token);
    
    try {
        if (!token.startsWith("Bearer ")) {
            log.warn("[{}] Token格式错误: {}", requestId, token);
            return ApiResponse.error(403, "Token格式错误");
        }
        
        String actualToken = token.substring(7);
        log.debug("[{}] 处理后的token: {}", requestId, actualToken);
        
        try {
            String studentId = jwtUtil.getStudentIdFromToken(actualToken);
            log.info("[{}] 成功解析学生ID: {}", requestId, studentId);
            
            log.debug("[{}] 开始查询数据库...", requestId);
            Map<String, Object> result = postService.getUserPosts(studentId, type, page, pageSize);
            log.debug("[{}] 数据库查询完成: {}", requestId, result);
            
            return ApiResponse.success(result);
            
        } catch (ExpiredJwtException e) {
            log.error("[{}] Token已过期: {}", requestId, e.getMessage());
            return ApiResponse.error(403, "Token已过期");
        } catch (JwtException e) {
            log.error("[{}] Token验证失败: {}", requestId, e.getMessage());
            return ApiResponse.error(403, "Token无效");
        }
        
    } catch (Exception e) {
        log.error("[{}] 处理请求失败", requestId, e);
        return ApiResponse.error(500, e.getMessage());
    } finally {
        log.info("[{}] >>> 请求处理完成 <<<", requestId);
    }
}
@DeleteMapping("/user/{postId}")
public ApiResponse<?> deleteUserPost(
        @PathVariable Long postId,
        @RequestHeader("Authorization") String token) {
    
    String requestId = UUID.randomUUID().toString().substring(0, 8);
    log.info("[{}] >>> 开始处理删除帖子请求 <<<", requestId);
    log.info("[{}] 帖子ID: {}", requestId, postId);
    
    try {
        if (!token.startsWith("Bearer ")) {
            log.warn("[{}] Token格式错误: {}", requestId, token);
            return ApiResponse.error(403, "Token格式错误");
        }
        
        String studentId = jwtUtil.getStudentIdFromToken(token.substring(7));
        log.info("[{}] 学生ID: {}", requestId, studentId);
        
        postService.deleteUserPost(studentId, postId);
        return ApiResponse.success("删除成功");
        
    } catch (ExpiredJwtException e) {
        log.error("[{}] Token已过期: {}", requestId, e.getMessage());
        return ApiResponse.error(403, "Token已过期");
    } catch (JwtException e) {
        log.error("[{}] Token验证失败: {}", requestId, e.getMessage());
        return ApiResponse.error(403, "Token无效");
    } catch (Exception e) {
        log.error("[{}] 删除帖子失败", requestId, e);
        return ApiResponse.error(500, e.getMessage());
    } finally {
        log.info("[{}] >>> 删除帖子请求处理完成 <<<", requestId);
    }
}
}