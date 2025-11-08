package com.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Article {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String image;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Boolean isPublished;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // transient
    private String authorName;
}
