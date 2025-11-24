package com.poetry.mapper;

import com.poetry.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserFollowMapper {
    UserFollow findByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    int insert(UserFollow follow);
    int deleteByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    int countFollowers(@Param("userId") Long userId);
    int countFollowing(@Param("userId") Long userId);
}
