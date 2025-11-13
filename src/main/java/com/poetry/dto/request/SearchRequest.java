package com.poetry.dto.request;

import lombok.Data;

@Data
public class SearchRequest {
    private String query;
    private int page = 1;
    private int size = 20;
}
