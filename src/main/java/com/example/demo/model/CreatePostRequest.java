package com.example.demo.model;

import lombok.Data;
import java.util.List;

@Data
public class CreatePostRequest {
    private String type;
    private String content;
    private List<String> images;
}