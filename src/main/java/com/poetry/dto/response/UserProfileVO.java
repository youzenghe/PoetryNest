package com.poetry.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileVO {
    private Long userId;
    private String username;
    private String avatar;
    private String bio;
    private String location;
    private LocalDate birthDate;
    private String preferredDynasty;
    private String favoriteStyle;
    private Long favoritePoemId;
    private String favoritePoemTitle;
    private Integer poemsReadCount;
    private Integer loginDays;
    private LocalDateTime createdAt;
    private List<String> favoritePoets;
}
