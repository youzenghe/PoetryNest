package com.poetry.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long articleId;
    private Long authorId;
    private String authorName;
    private String content;
    private Long parentId;
    private Boolean isReply;
    private LocalDateTime createdAt;
    private List<CommentVO> replies;
}
