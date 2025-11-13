package com.poetry.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserPurchaseVO {
    private Long id;
    private Long userId;
    private String username;
    private Long poemId;
    private String poemTitle;
    private String poemAuthor;
    private String orderNo;
    private BigDecimal amount;
    private String status;
    private String payMethod;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiredAt;
}
