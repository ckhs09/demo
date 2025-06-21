package com.example.demo.repository;

import com.example.demo.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 根据类型查询所有帖子（支持type为空）
    @Query("SELECT p FROM Post p WHERE (:type IS NULL OR p.type = :type)")
    Page<Post> findByTypeWithNull(@Param("type") String type, Pageable pageable);
    
    // 查询指定用户的所有帖子
    @Query("SELECT p FROM Post p WHERE p.studentId = :studentId")
    Page<Post> findByStudentId(@Param("studentId") String studentId, Pageable pageable);
    
    // 查询指定用户的指定类型帖子（支持type为空）
    @Query("SELECT p FROM Post p WHERE p.studentId = :studentId AND (:type IS NULL OR p.type = :type)")
    Page<Post> findByStudentIdAndTypeWithNull(
        @Param("studentId") String studentId, 
        @Param("type") String type, 
        Pageable pageable
    );
    
    // 查询指定用户的指定类型帖子
    @Query("SELECT p FROM Post p WHERE p.studentId = :studentId AND p.type = :type")
    Page<Post> findByStudentIdAndType(
        @Param("studentId") String studentId, 
        @Param("type") String type, 
        Pageable pageable
    );
    
    // 检查点赞状态
    @Query("SELECT COUNT(p) > 0 FROM Post p WHERE p.id = :postId AND p.studentId = :studentId")
    boolean existsByPostIdAndStudentId(
        @Param("postId") Long postId, 
        @Param("studentId") String studentId
    );
}