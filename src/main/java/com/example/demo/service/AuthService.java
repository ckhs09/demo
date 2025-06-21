package com.example.demo.service;
import com.example.demo.entity.Student;
import com.example.demo.model.UpdateUserRequest;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.RegisterRequest;
import com.example.demo.model.StudentVO;
import com.example.demo.repository.StudentRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AuthService {
   
   @Autowired
   private StudentRepository studentRepository;
   
   @Autowired
   private JwtUtil jwtUtil;
   
   @Value("${file.upload.path:uploads}")
   private String uploadBasePath;
   
   public String handleAvatar(MultipartFile file) throws Exception {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始处理头像上传 <<<", requestId);
       
       // 验证文件类型
       String contentType = file.getContentType();
       if (contentType == null || !contentType.startsWith("image/")) {
           throw new IllegalArgumentException("只能上传图片文件");
       }
       
       // 生成文件名
       String extension = contentType.substring(contentType.lastIndexOf('/') + 1);
       String filename = String.format("avatar_%s_%s.%s", 
           System.currentTimeMillis(), 
           UUID.randomUUID().toString().substring(0, 8),
           extension);
           
       // 确保目录存在
       String uploadDir = uploadBasePath + "/avatars/";
       File dir = new File(uploadDir);
       if (!dir.exists()) {
           dir.mkdirs();
       }
       
       // 保存文件
       File destFile = new File(dir.getAbsolutePath() + File.separator + filename);
       file.transferTo(destFile);
       
       // 生成访问URL
       String avatarUrl = "/uploads/avatars/" + filename;
       log.info("[{}] >>> 头像上传完成: {} <<<", requestId, avatarUrl);
       return avatarUrl;
   }
   
   public void register(RegisterRequest request) {
       // 验证学号是否已存在
       if (studentRepository.existsByStudentId(request.getStudentId())) {
           throw new IllegalArgumentException("该学号已被注册");
       }
       
       // 创建新用户
       Student student = new Student();
       student.setStudentId(request.getStudentId());
       student.setName(request.getName());
       student.setMajor(request.getMajor());
       student.setGrade(request.getGrade());
       student.setPassword(request.getPassword());
       student.setCreateTime(System.currentTimeMillis());
       student.setAvatar(request.getAvatar());  // 设置头像URL
       
       // 保存到数据库
       studentRepository.save(student);
   }
    public LoginResponse login(LoginRequest request) {
       // 查找用户
       Student student = studentRepository.findByStudentId(request.getStudentId())
           .orElseThrow(() -> new IllegalArgumentException("学号或密码错误"));
       
       // 验证密码
       if (!student.getPassword().equals(request.getPassword())) {
           throw new IllegalArgumentException("学号或密码错误");
       }
       
       // 生成 JWT token
       String token = jwtUtil.generateToken(student.getStudentId());
       
       // 创建登录响应
       LoginResponse response = new LoginResponse();
       response.setToken(token);
       response.setUserInfo(convertToVO(student));
       
       return response;
   }
   
   public void updateUser(String studentId, UpdateUserRequest request) {
       Student student = studentRepository.findByStudentId(studentId)
           .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
       
       // 更新基本信息
       if (request.getName() != null) {
           student.setName(request.getName());
       }
       if (request.getMajor() != null) {
           student.setMajor(request.getMajor());
       }
       if (request.getGrade() != null) {
           student.setGrade(request.getGrade());
       }
       if (request.getBio() != null) {
           student.setBio(request.getBio());
       }
       if (request.getTags() != null) {
           student.setTags(request.getTags());
       }
       
       // 如果要修改密码
       if (request.getNewPassword() != null) {
           if (!student.getPassword().equals(request.getOldPassword())) {
               throw new IllegalArgumentException("原密码错误");
           }
           student.setPassword(request.getNewPassword());
       }
       
       studentRepository.save(student);
   }
    public String updateAvatar(String studentId, MultipartFile file) throws Exception {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始处理头像上传 <<<", requestId);
       
       // 验证文件类型
       String contentType = file.getContentType();
       if (contentType == null || !contentType.startsWith("image/")) {
           throw new IllegalArgumentException("只能上传图片文件");
       }
       
       // 生成文件名
       String extension = contentType.substring(contentType.lastIndexOf('/') + 1);
       String filename = String.format("avatar_%s_%s.%s", 
           studentId, 
           System.currentTimeMillis(), 
           extension);
           
       // 确保目录存在
       String uploadDir = uploadBasePath + "/avatars/";
       File dir = new File(uploadDir);
       if (!dir.exists()) {
           dir.mkdirs();
       }
       
       // 保存文件
       File destFile = new File(dir.getAbsolutePath() + File.separator + filename);
       file.transferTo(destFile);
       
       // 生成访问URL
       String avatarUrl = "/uploads/avatars/" + filename;
       
       // 更新数据库
       Student student = studentRepository.findByStudentId(studentId)
           .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
           
       // 删除旧头像文件
       if (student.getAvatar() != null) {
           String oldFileName = student.getAvatar().substring(student.getAvatar().lastIndexOf('/') + 1);
           File oldFile = new File(dir, oldFileName);
           if (oldFile.exists()) {
               oldFile.delete();
           }
       }
       
       student.setAvatar(avatarUrl);
       studentRepository.save(student);
       
       log.info("[{}] >>> 头像上传完成: {} <<<", requestId, avatarUrl);
       return avatarUrl;
   }
   
   public String updateCoverImage(String studentId, MultipartFile file) throws Exception {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始处理封面图上传 <<<", requestId);
       
       // 验证文件类型
       String contentType = file.getContentType();
       if (contentType == null || !contentType.startsWith("image/")) {
           throw new IllegalArgumentException("只能上传图片文件");
       }
       
       // 生成文件名
       String extension = contentType.substring(contentType.lastIndexOf('/') + 1);
       String filename = String.format("cover_%s_%s.%s", 
           studentId, 
           System.currentTimeMillis(), 
           extension);
           
       // 确保目录存在
       String uploadDir = uploadBasePath + "/covers/";
       File dir = new File(uploadDir);
       if (!dir.exists()) {
           dir.mkdirs();
       }
       
       // 保存文件
       File destFile = new File(dir.getAbsolutePath() + File.separator + filename);
       file.transferTo(destFile);
       
       // 生成访问URL
       String coverUrl = "/uploads/covers/" + filename;
       
       // 更新数据库
       Student student = studentRepository.findByStudentId(studentId)
           .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
           
       // 删除旧封面图文件
       if (student.getCoverImage() != null) {
           String oldFileName = student.getCoverImage().substring(student.getCoverImage().lastIndexOf('/') + 1);
           File oldFile = new File(dir, oldFileName);
           if (oldFile.exists()) {
               oldFile.delete();
           }
       }
       
       student.setCoverImage(coverUrl);
       studentRepository.save(student);
       
       log.info("[{}] >>> 封面图上传完成: {} <<<", requestId, coverUrl);
       return coverUrl;
   }
   
   public Map<String, Object> getUserInfo(String studentId) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] >>> 开始获取用户信息, studentId: {} <<<", requestId, studentId);
       
       try {
           Student student = studentRepository.findByStudentId(studentId)
               .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
               
           Map<String, Object> userInfo = new HashMap<>();
           userInfo.put("id", student.getStudentId());
           userInfo.put("name", student.getName());
           userInfo.put("major", student.getMajor());
           userInfo.put("grade", student.getGrade());
           userInfo.put("avatar", student.getAvatar());
           userInfo.put("coverImage", student.getCoverImage());
           userInfo.put("bio", student.getBio());
           userInfo.put("tags", student.getTags());
           
           log.info("[{}] >>> 获取用户信息成功 <<<", requestId);
           return userInfo;
           
       } catch (Exception e) {
           log.error("[{}] !!! 获取用户信息失败: {} <<<", requestId, e.getMessage(), e);
           throw e;
       }
   }
    private StudentVO convertToVO(Student student) {
       StudentVO vo = new StudentVO();
       vo.setStudentId(student.getStudentId());
       vo.setName(student.getName());
       vo.setMajor(student.getMajor());
       vo.setGrade(student.getGrade());
       vo.setAvatar(student.getAvatar());
       vo.setCoverImage(student.getCoverImage());
       vo.setBio(student.getBio());
       vo.setTags(student.getTags());
       return vo;
   }
}