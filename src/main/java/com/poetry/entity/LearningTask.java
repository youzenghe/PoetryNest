package com.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearningTask {
    private Long id;
    private Long userId;
    private Long poemId;
    private String title;
    private String status;
    private LocalDateTime deadline;
    private Boolean reminded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // transient fields for display
    private transient String poemTitle;
    private transient String username;
}
