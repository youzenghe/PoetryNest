package com.poetry.mapper;

import com.poetry.entity.PoemAnnotation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PoemAnnotationMapper {
    PoemAnnotation findById(@Param("id") Long id);
    List<PoemAnnotation> findByPoemId(@Param("poemId") Long poemId);
    int insert(PoemAnnotation annotation);
    int update(PoemAnnotation annotation);
    int deleteById(@Param("id") Long id);
}
