package com.poetry.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PoemAnnotationVO {
    private Long id;
    private Long poemId;
    private String poemTitle;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
