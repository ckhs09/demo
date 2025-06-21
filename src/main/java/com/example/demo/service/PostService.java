package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostLike;
import com.example.demo.entity.Student;
import com.example.demo.model.CreatePostRequest;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostLikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.demo.entity.Comment;
import com.example.demo.model.CreateCommentRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void createPost(String studentId, CreatePostRequest request) throws Exception {
        Post post = new Post();
        post.setStudentId(studentId);
        post.setType(request.getType());
        post.setContent(request.getContent());
        post.setImages(objectMapper.writeValueAsString(request.getImages()));
        post.setCreateTime(System.currentTimeMillis());
        
        postRepository.save(post);
    }

    public Map<String, Object> getPosts(String type, Integer page, Integer pageSize, String sortBy, String currentStudentId) {
        Sort sort = "hot".equals(sortBy) 
            ? Sort.by(Sort.Direction.DESC, "likeCount", "createTime")
            : Sort.by(Sort.Direction.DESC, "createTime");
            
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Post> postPage = postRepository.findByTypeWithNull(type, pageable);
        
        List<Map<String, Object>> list = postPage.getContent().stream()
            .map(post -> convertPostToMap(post, currentStudentId))
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", postPage.getTotalElements());
        return result;
    }

    @Transactional
    public Map<String, Object> toggleLike(Long postId, String studentId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
            
        boolean isLiked = postLikeRepository.existsByPostIdAndStudentId(postId, studentId);
        
        if (isLiked) {
            postLikeRepository.deleteByPostIdAndStudentId(postId, studentId);
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setStudentId(studentId);
            like.setCreateTime(System.currentTimeMillis());
            postLikeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        
        post = postRepository.save(post);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", !isLiked);
        result.put("likeCount", post.getLikeCount());
        return result;
    }

    public Map<String, Object> getPostDetail(Long postId, String currentStudentId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        
        return convertPostToMap(post, currentStudentId);
    }

    public List<Map<String, Object>> getComments(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreateTimeDesc(postId);
        
        Map<Long, List<Comment>> repliesMap = comments.stream()
            .filter(c -> c.getReplyToId() != null)
            .collect(Collectors.groupingBy(Comment::getReplyToId));
            
        return comments.stream()
            .filter(c -> c.getReplyToId() == null)
            .map(comment -> convertCommentToMap(comment, repliesMap))
            .collect(Collectors.toList());
    }

    @Transactional
    public void createComment(Long postId, String studentId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        
        if (request.getReplyToId() != null) {
            commentRepository.findById(request.getReplyToId())
                .orElseThrow(() -> new IllegalArgumentException("被回复的评论不存在"));
        }
        
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setStudentId(studentId);
        comment.setContent(request.getContent());
        comment.setReplyToId(request.getReplyToId());
        comment.setCreateTime(System.currentTimeMillis());
        
        commentRepository.save(comment);
        
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
    }

    public Map<String, Object> getUserPosts(String studentId, String type, Integer page, Integer pageSize) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        log.info("[{}] 开始查询用户帖子, studentId={}, type={}, page={}", 
                requestId, studentId, type, page);
        
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Post> postPage;
            
            if (type != null && !type.isEmpty()) {
                postPage = postRepository.findByStudentIdAndType(studentId, type, pageable);
            } else {
                postPage = postRepository.findByStudentId(studentId, pageable);
            }
            
            log.info("[{}] 查询到 {} 条记录", requestId, postPage.getContent().size());
            
            List<Map<String, Object>> list = postPage.getContent().stream()
                .map(post -> convertPostToMap(post, studentId))
                .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", postPage.getTotalElements());
            
            return result;
            
        } catch (Exception e) {
            log.error("[{}] 查询用户帖子失败: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    private Map<String, Object> convertPostToMap(Post post, String currentStudentId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.getId());
        map.put("type", post.getType());
        map.put("content", post.getContent());
        map.put("createTime", post.getCreateTime());
        map.put("likeCount", post.getLikeCount());
        map.put("commentCount", post.getCommentCount());
        
        try {
            map.put("images", objectMapper.readValue(post.getImages(), List.class));
        } catch (Exception e) {
            map.put("images", new ArrayList<>());
        }
        
        // 获取发帖人信息
        Student student = studentRepository.findByStudentId(post.getStudentId()).orElse(null);
        if (student != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", student.getStudentId());
            userMap.put("name", student.getName());
            userMap.put("avatar", student.getAvatar());
            userMap.put("coverImage", student.getCoverImage());
            userMap.put("bio", student.getBio());
            userMap.put("tags", student.getTags());
            
            map.put("user", userMap);
        }
        
        // 判断当前用户是否点赞
        if (currentStudentId != null) {
            map.put("isLiked", postLikeRepository.existsByPostIdAndStudentId(post.getId(), currentStudentId));
        }
        
        return map;
    }

    private Map<String, Object> convertCommentToMap(Comment comment, Map<Long, List<Comment>> repliesMap) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("content", comment.getContent());
        map.put("createTime", comment.getCreateTime());
        
        Student student = studentRepository.findByStudentId(comment.getStudentId()).orElse(null);
        if (student != null) {
            Map<String, Object> authorMap = new HashMap<>();
            authorMap.put("id", student.getStudentId());
            authorMap.put("name", student.getName());
            authorMap.put("avatar", student.getAvatar());
            authorMap.put("coverImage", student.getCoverImage());
            authorMap.put("bio", student.getBio());
            authorMap.put("tags", student.getTags());
            
            map.put("author", authorMap);
        }
        
        List<Comment> replies = repliesMap.getOrDefault(comment.getId(), new ArrayList<>());
        if (!replies.isEmpty()) {
            map.put("replies", replies.stream()
                .map(this::convertReplyToMap)
                .collect(Collectors.toList()));
        }
        
        return map;
    }

    private Map<String, Object> convertReplyToMap(Comment reply) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", reply.getId());
        map.put("content", reply.getContent());
        map.put("createTime", reply.getCreateTime());
        
        Student student = studentRepository.findByStudentId(reply.getStudentId()).orElse(null);
        if (student != null) {
            Map<String, Object> authorMap = new HashMap<>();
            authorMap.put("id", student.getStudentId());
            authorMap.put("name", student.getName());
            authorMap.put("avatar", student.getAvatar());
            authorMap.put("coverImage", student.getCoverImage());
            authorMap.put("bio", student.getBio());
            authorMap.put("tags", student.getTags());
            
            map.put("author", authorMap);
        }
        
        return map;
    }
    public void deleteUserPost(String studentId, Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));
            
        // 验证是否是用户自己的帖子
        if (!post.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权删除此帖子");
        }
        
        // 删除帖子
        postRepository.deleteById(postId);
    }
}