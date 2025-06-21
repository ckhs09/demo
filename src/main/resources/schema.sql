-- 用户表
CREATE TABLE student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    major VARCHAR(100),
    grade VARCHAR(20),
    avatar VARCHAR(255),
    cover_image VARCHAR(255),
    bio TEXT,
    tags VARCHAR(255),
    created_at BIGINT
);

-- 帖子表
CREATE TABLE post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    images JSON,
    create_time BIGINT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0
);

-- 评论表
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    student_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    reply_to_id BIGINT,
    create_time BIGINT NOT NULL
);

-- APP版本表
CREATE TABLE app_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version VARCHAR(20) NOT NULL,
    force_update BOOLEAN NOT NULL DEFAULT FALSE,
    download_url VARCHAR(255) NOT NULL,
    update_content TEXT,
    release_time BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT FALSE
);

-- 点赞表
CREATE TABLE post_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    student_id VARCHAR(50) NOT NULL,
    create_time BIGINT NOT NULL,
    UNIQUE KEY uk_post_student (post_id, student_id)
);

-- 添加索引
ALTER TABLE post ADD INDEX idx_student_id (student_id);
ALTER TABLE post ADD INDEX idx_create_time (create_time);
ALTER TABLE comments ADD INDEX idx_post_id (post_id);
ALTER TABLE comments ADD INDEX idx_student_id (student_id);
ALTER TABLE post_like ADD INDEX idx_student_id (student_id);