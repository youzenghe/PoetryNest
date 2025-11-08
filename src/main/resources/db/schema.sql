CREATE DATABASE IF NOT EXISTS poetry DEFAULT CHARACTER SET utf8mb4;
USE poetry;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 诗词表
CREATE TABLE IF NOT EXISTS poem (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    paragraphs TEXT NOT NULL,
    INDEX idx_author (author),
    FULLTEXT INDEX ft_poem (title, author, paragraphs) WITH PARSER ngram
);

-- 3. 用户资料表
CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    avatar VARCHAR(500) DEFAULT '',
    bio VARCHAR(500) DEFAULT '',
    location VARCHAR(100) DEFAULT '',
    birth_date DATE NULL,
    preferred_dynasty VARCHAR(20) DEFAULT '',
    favorite_style VARCHAR(20) DEFAULT '',
    favorite_poem_id BIGINT NULL,
    poems_read_count INT DEFAULT 0,
    login_days INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (favorite_poem_id) REFERENCES poem(id) ON DELETE SET NULL
);

-- 4. 喜欢的诗人
CREATE TABLE IF NOT EXISTS favorite_poet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_profile_id BIGINT NOT NULL,
    poet_name VARCHAR(50) NOT NULL,
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_profile_poet (user_profile_id, poet_name),
    FOREIGN KEY (user_profile_id) REFERENCES user_profile(id) ON DELETE CASCADE
);

-- 5. 用户收藏诗词
CREATE TABLE IF NOT EXISTS user_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    poem_id BIGINT NOT NULL,
    favorite_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_poem (user_id, poem_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (poem_id) REFERENCES poem(id) ON DELETE CASCADE
);

-- 6. 社区文章
CREATE TABLE IF NOT EXISTS article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    image VARCHAR(500) DEFAULT '',
    views_count INT DEFAULT 0,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    is_published TINYINT(1) DEFAULT 1,
    is_featured TINYINT(1) DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES user(id),
    INDEX idx_author_published (author_id, is_published),
    INDEX idx_created (created_at)
);

-- 7. 评论
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    parent_id BIGINT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES user(id),
    FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE CASCADE,
    INDEX idx_article (article_id)
);

-- 8. 文章点赞
CREATE TABLE IF NOT EXISTS article_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_article_user (article_id, user_id),
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 9. 用户关注
CREATE TABLE IF NOT EXISTS user_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 10. 诗词注解表
CREATE TABLE IF NOT EXISTS poem_annotation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    poem_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL COMMENT '注解标题',
    content TEXT NOT NULL COMMENT '注解内容',
    author_id BIGINT NOT NULL COMMENT '创建者(管理员)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (poem_id) REFERENCES poem(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES user(id),
    INDEX idx_poem (poem_id)
);

-- 11. 用户购买/选修记录表（伪微信支付）
CREATE TABLE IF NOT EXISTS user_purchase (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    poem_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL COMMENT '订单号(模拟微信)',
    amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '金额',
    status VARCHAR(20) DEFAULT 'SUCCESS' COMMENT 'SUCCESS/CANCELLED',
    pay_method VARCHAR(20) DEFAULT 'WECHAT_MINI' COMMENT '支付方式',
    purchased_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expired_at DATETIME NULL COMMENT '过期时间',
    UNIQUE KEY uk_user_poem (user_id, poem_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (poem_id) REFERENCES poem(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_order (order_no)
);

-- 12. 学习任务表
CREATE TABLE IF NOT EXISTS learning_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    poem_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/COMPLETED/CANCELLED',
    deadline DATETIME NOT NULL COMMENT '截止时间',
    reminded TINYINT(1) DEFAULT 0 COMMENT '是否已提醒',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (poem_id) REFERENCES poem(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_deadline (deadline)
);
