package com.poetry.mapper;

import com.poetry.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(@Param("id") Long id);
    User findByUsername(@Param("username") String username);
    int insert(User user);
    int countByUsername(@Param("username") String username);

    // Admin methods
    List<User> findAllPaged();
    int updateRole(@Param("id") Long id, @Param("role") String role);
}
