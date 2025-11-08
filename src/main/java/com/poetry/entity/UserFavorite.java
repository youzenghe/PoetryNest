package com.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserFavorite {
    private Long id;
    private Long userId;
    private Long poemId;
    private LocalDateTime favoriteTime;

    // transient
    private String poemTitle;
    private String poemAuthor;
    private String poemParagraphs;
}
