package com.poetry.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminPurchaseRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "诗词ID不能为空")
    private Long poemId;
    private BigDecimal amount;
    private LocalDateTime expiredAt;
}
