package com.poetry.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserPurchase {
    private Long id;
    private Long userId;
    private Long poemId;
    private String orderNo;
    private BigDecimal amount;
    private String status;
    private String payMethod;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiredAt;

    // transient fields for display
    private transient String poemTitle;
    private transient String poemAuthor;
    private transient String username;
}
