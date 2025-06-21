package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;

@Component
public class RoleUtil {
@Autowired    

    private StudentRepository studentRepository;
    public String getCurrentStudentId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    public boolean isAdmin(String studentId) {
        // 从数据库中查询用户角色
        Student student = studentRepository.findByStudentId(studentId)
            .orElse(null);
        return student != null && "ADMIN".equals(student.getRole());
    }
}