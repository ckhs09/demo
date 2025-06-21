package com.example.demo.model;
import lombok.Data;

@Data
public class CreateCommentRequest {
    private String content;
    private Long replyToId;  // 可选，回复某条评论时使用
}