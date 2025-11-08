package com.poetry.entity;

import lombok.Data;

@Data
public class Poem {
    private Long id;
    private String title;
    private String author;
    private String paragraphs;
}
