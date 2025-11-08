package com.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoritePoet {
    private Long id;
    private Long userProfileId;
    private String poetName;
    private LocalDateTime addedAt;
}
