package com.poetry.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
