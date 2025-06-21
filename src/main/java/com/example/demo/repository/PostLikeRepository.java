package com.example.demo.repository;

import com.example.demo.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndStudentId(Long postId, String studentId);
    void deleteByPostIdAndStudentId(Long postId, String studentId);
}