package com.poetry.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String image;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
