package com.poetry.mapper;

import com.poetry.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {
    List<UserFavorite> findByUserId(@Param("userId") Long userId);
    UserFavorite findByUserAndPoem(@Param("userId") Long userId, @Param("poemId") Long poemId);
    int insert(UserFavorite favorite);
    int deleteByUserAndPoem(@Param("userId") Long userId, @Param("poemId") Long poemId);
    int countByUserAndPoem(@Param("userId") Long userId, @Param("poemId") Long poemId);
    List<UserFavorite> findAll();
}
