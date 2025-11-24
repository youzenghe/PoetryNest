package com.poetry.mapper;

import com.poetry.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleLikeMapper {
    ArticleLike findByArticleAndUser(@Param("articleId") Long articleId, @Param("userId") Long userId);
    int insert(ArticleLike like);
    int deleteByArticleAndUser(@Param("articleId") Long articleId, @Param("userId") Long userId);
    int countByArticleId(@Param("articleId") Long articleId);
}
