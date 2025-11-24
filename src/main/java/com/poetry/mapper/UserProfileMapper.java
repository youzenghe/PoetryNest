package com.poetry.mapper;

import com.poetry.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {
    UserProfile findByUserId(@Param("userId") Long userId);
    int insert(UserProfile profile);
    int update(UserProfile profile);
    int incrementLoginDays(@Param("userId") Long userId);
}
