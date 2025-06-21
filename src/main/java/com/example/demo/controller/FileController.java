package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileController {
    
    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;
    
    @PostMapping("/image")
    public ApiResponse<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        log.info("[{}] >>> 开始上传图片 <<<", requestId);
        
        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error(400, "只能上传图片文件");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 确定保存目录
            String uploadDir = uploadBasePath + "/images/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(dir.getAbsolutePath() + File.separator + filename);
            file.transferTo(destFile);
            
            // 返回文件URL
            String fileUrl = "/uploads/images/" + filename;
            log.info("[{}] 图片上传成功: {}", requestId, fileUrl);
            return ApiResponse.success(fileUrl);
            
        } catch (Exception e) {
            log.error("[{}] 图片上传失败: {}", requestId, e.getMessage(), e);
            return ApiResponse.error(500, "文件上传失败");
        }
    }
}