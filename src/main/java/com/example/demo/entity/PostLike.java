package com.example.demo.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "post_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"postId", "studentId"})
})
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long postId;
    
    @Column(nullable = false)
    private String studentId;
    
    @Column(nullable = false)
    private Long createTime;
}
