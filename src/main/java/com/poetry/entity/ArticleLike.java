package com.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleLike {
    private Long id;
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;
}
