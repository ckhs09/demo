package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   
   private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
   
   @Value("${file.upload.path:uploads}")
   private String uploadPath;
   
   @Override
   public void addResourceHandlers(ResourceHandlerRegistry registry) {
       try {
           // 获取绝对路径
           File uploadDir = new File(uploadPath);
           String absolutePath = uploadDir.getAbsolutePath();
           
           // 确保目录存在
           if (!uploadDir.exists()) {
               uploadDir.mkdirs();
               logger.info("Created upload directory: " + absolutePath);
           }
           
           // 打印实际的上传路径
           logger.info("Upload path: " + absolutePath);
           
           // 确保路径以文件分隔符结尾
           if (!absolutePath.endsWith(File.separator)) {
               absolutePath = absolutePath + File.separator;
           }
           
           // 注意：这里移除了 /api 前缀，因为已经在 context-path 中定义
           registry.addResourceHandler("/uploads/**")
               .addResourceLocations("file:" + absolutePath)
               .setCachePeriod(3600)
               .resourceChain(true);
               
           logger.info("Resource handler configured with path: file:" + absolutePath);
           
       } catch (Exception e) {
           logger.error("Error configuring resource handlers", e);
       }
   }
}