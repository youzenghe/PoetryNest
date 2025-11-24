package com.poetry.mapper;

import com.poetry.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> findByArticleId(@Param("articleId") Long articleId);
    List<Comment> findTopLevelByArticleId(@Param("articleId") Long articleId);
    List<Comment> findReplies(@Param("parentId") Long parentId);
    int insert(Comment comment);
    int countActiveByArticleId(@Param("articleId") Long articleId);
}
