package com.poetry.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class QuizVO {
    private Long questionId;
    private String type;
    private String question;
    private List<String> options;
    private Integer correctIndex;
    private String explanation;
    private Long poemId;
    private String poemTitle;
    private String poemAuthor;
}
