package com.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long articleId;
    private Long authorId;
    private String content;
    private Long parentId;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // transient
    private String authorName;
    private java.util.List<Comment> replies;
}
