package com.poetry.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleCreateRequest {
    @NotBlank(message = "标题不能为空")
    @Size(min = 5, message = "标题至少需要5个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(min = 20, message = "内容至少需要20个字符")
    private String content;

    private String image;
}
