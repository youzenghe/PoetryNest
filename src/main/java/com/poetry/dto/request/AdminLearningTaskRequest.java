package com.poetry.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminLearningTaskRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "诗词ID不能为空")
    private Long poemId;
    @NotBlank(message = "任务标题不能为空")
    private String title;
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;
}
