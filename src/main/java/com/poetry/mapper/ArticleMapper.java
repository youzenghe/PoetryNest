package com.poetry.mapper;

import com.poetry.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    Article findById(@Param("id") Long id);
    List<Article> findPublished(@Param("query") String query);
    List<Article> findPopular(@Param("limit") int limit);
    List<Article> findByAuthorId(@Param("authorId") Long authorId);
    int insert(Article article);
    int incrementViews(@Param("id") Long id);
    int updateLikesCount(@Param("id") Long id, @Param("count") int count);
    int updateCommentsCount(@Param("id") Long id, @Param("count") int count);
    int updateArticle(Article article);
    int deleteById(@Param("id") Long id);
}
