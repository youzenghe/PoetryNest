package com.poetry.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearningTaskVO {
    private Long id;
    private Long userId;
    private String username;
    private Long poemId;
    private String poemTitle;
    private String title;
    private String status;
    private LocalDateTime deadline;
    private Boolean reminded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
