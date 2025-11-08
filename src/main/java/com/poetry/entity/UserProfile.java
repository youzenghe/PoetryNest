package com.poetry.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserProfile {
    private Long id;
    private Long userId;
    private String avatar;
    private String bio;
    private String location;
    private LocalDate birthDate;
    private String preferredDynasty;
    private String favoriteStyle;
    private Long favoritePoemId;
    private Integer poemsReadCount;
    private Integer loginDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // transient fields for display
    private String username;
    private String favoritePoemTitle;
}
