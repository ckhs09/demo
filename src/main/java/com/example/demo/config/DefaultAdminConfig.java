package com.example.demo.config;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultAdminConfig {

    @Autowired
    private StudentRepository studentRepository;

    @Bean
    public CommandLineRunner createDefaultAdmin() {
        return args -> {
            // 检查是否已经存在管理员
            if (!studentRepository.findByStudentId("admin").isPresent()) {
                // 创建默认管理员
                Student admin = new Student();
                admin.setStudentId("2023102291");
                admin.setPassword("ssbadmin"); // 注意：实际使用时需要加密
                admin.setName("默认管理员");
                admin.setMajor("系统管理");
                admin.setGrade("2023");
                admin.setAvatar("http://example.com/default-avatar.jpg");
                admin.setRole("ADMIN");
                admin.setCreateTime(System.currentTimeMillis());

                // 保存到数据库
                studentRepository.save(admin);
                System.out.println("默认管理员已创建");
            } else {
                System.out.println("默认管理员已存在");
            }
        };
    }
}