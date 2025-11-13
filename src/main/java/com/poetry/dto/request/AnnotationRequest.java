package com.poetry.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnotationRequest {
    @NotNull(message = "诗词ID不能为空")
    private Long poemId;
    @NotBlank(message = "注解标题不能为空")
    private String title;
    @NotBlank(message = "注解内容不能为空")
    private String content;
}
