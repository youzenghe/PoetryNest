package com.poetry.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PoemCreateRequest {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "作者不能为空")
    private String author;
    @NotBlank(message = "内容不能为空")
    private String paragraphs;
}
