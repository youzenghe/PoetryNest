package com.poetry.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ProfileUpdateRequest {
    private String bio;
    private String location;
    private String birthDate;
    private String preferredDynasty;
    private String favoriteStyle;
    private Long favoritePoemId;
    private List<String> favoritePoets;
}
