package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long postId;
    
    @Column(nullable = false)
    private String studentId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column
    private Long replyToId;
    
    @Column(nullable = false)
    private Long createTime;
}