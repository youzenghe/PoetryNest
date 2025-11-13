package com.poetry.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequest {
    @NotNull(message = "诗词ID不能为空")
    private Long poemId;
}
