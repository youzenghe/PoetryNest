package com.poetry.mapper;

import com.poetry.entity.FavoritePoet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoritePoetMapper {
    List<FavoritePoet> findByProfileId(@Param("userProfileId") Long userProfileId);
    int insert(FavoritePoet poet);
    int deleteByProfileIdAndName(@Param("userProfileId") Long userProfileId, @Param("poetName") String poetName);
    int deleteByProfileId(@Param("userProfileId") Long userProfileId);
}
