package com.poetry.mapper;

import com.poetry.entity.UserPurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserPurchaseMapper {
    UserPurchase findById(@Param("id") Long id);
    List<UserPurchase> findByUserId(@Param("userId") Long userId);
    UserPurchase findByUserAndPoem(@Param("userId") Long userId, @Param("poemId") Long poemId);
    UserPurchase findByOrderNo(@Param("orderNo") String orderNo);
    int insert(UserPurchase purchase);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    int countByUserAndPoem(@Param("userId") Long userId, @Param("poemId") Long poemId);
    List<UserPurchase> findAll();
}
