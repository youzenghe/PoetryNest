package com.poetry.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PoemVO {
    private Long id;
    private String title;
    private String author;
    private String paragraphs;
    private Boolean isFavorite;
    private List<PoemVO> relatedPoems;
}
