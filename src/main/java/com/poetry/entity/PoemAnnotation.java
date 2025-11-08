package com.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PoemAnnotation {
    private Long id;
    private Long poemId;
    private String title;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // transient fields for display
    private transient String authorName;
    private transient String poemTitle;
}
