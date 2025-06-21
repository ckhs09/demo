package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.RegisterRequest;
import com.example.demo.repository.StudentRepository;
import com.example.demo.util.RoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private RoleUtil roleUtil;
    
    // 注册管理员接口
    @PostMapping("/register-admin")
    public ApiResponse<?> registerAdmin(@RequestBody RegisterRequest request) {
        // 检查是否已存在
        if (studentRepository.findByStudentId(request.getStudentId()).isPresent()) {
            return ApiResponse.error(400, "该学号已注册");
        }
        
        // 创建管理员账号
        Student student = new Student();
        student.setStudentId(request.getStudentId());
        student.setPassword(request.getPassword());  // 注意：实际使用时需要加密
        student.setName(request.getName());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        student.setAvatar(request.getAvatar());
        student.setRole("ADMIN");  // 直接设置为管理员
        student.setCreateTime(System.currentTimeMillis());
        
        studentRepository.save(student);
        return ApiResponse.success("管理员注册成功");
    }
    
    // 获取所有管理员
    @GetMapping("/admins")
    public ApiResponse<?> getAllAdmins() {
        String currentStudentId = roleUtil.getCurrentStudentId();
        if (!roleUtil.isAdmin(currentStudentId)) {
            return ApiResponse.error(403, "无权限查看管理员列表");
        }
        
        List<Student> admins = studentRepository.findAll().stream()
            .filter(student -> "ADMIN".equals(student.getRole()))
            .collect(Collectors.toList());
            
        return ApiResponse.success(admins);
    }
    
    // 删除管理员
    @DeleteMapping("/admin/{studentId}")
    public ApiResponse<?> removeAdmin(@PathVariable String studentId) {
        String currentStudentId = roleUtil.getCurrentStudentId();
        if (!roleUtil.isAdmin(currentStudentId)) {
            return ApiResponse.error(403, "无权限删除管理员");
        }
        
        Student student = studentRepository.findByStudentId(studentId)
            .orElse(null);
        if (student == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        // 防止删除自己
        if (studentId.equals(currentStudentId)) {
            return ApiResponse.error(400, "不能删除自己的管理员权限");
        }
        
        student.setRole("USER");
        studentRepository.save(student);
        return ApiResponse.success("管理员权限已移除");
    }
    
    // 修改管理员信息
    @PutMapping("/admin/{studentId}")
    public ApiResponse<?> updateAdmin(@PathVariable String studentId, @RequestBody RegisterRequest request) {
        String currentStudentId = roleUtil.getCurrentStudentId();
        if (!roleUtil.isAdmin(currentStudentId)) {
            return ApiResponse.error(403, "无权限修改管理员信息");
        }
        
        Student student = studentRepository.findByStudentId(studentId)
            .orElse(null);
        if (student == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        // 更新信息
        student.setName(request.getName());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            student.setPassword(request.getPassword());
        }
        if (request.getAvatar() != null) {
            student.setAvatar(request.getAvatar());
        }
        
        studentRepository.save(student);
        return ApiResponse.success("管理员信息更新成功");
    }
    
    // 检查用户角色
    @GetMapping("/check-role")
    public ApiResponse<?> checkRole(@RequestParam String studentId) {
        logger.info("checkRole 方法被调用，studentId: " + studentId);
        // 直接查找用户角色，不进行身份验证
        logger.info("check role of student: " + studentId);
        
        Student student = studentRepository.findByStudentId(studentId).orElse(null);
        if (student != null) {
            logger.info("找到用户：" + student.getName());
            return ApiResponse.success(student.getRole());
        }
        return ApiResponse.error(404, "用户不存在");
    }
}