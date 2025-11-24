package com.poetry.mapper;

import com.poetry.entity.Poem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PoemMapper {
    Poem findById(@Param("id") Long id);
    List<Poem> search(@Param("query") String query);
    List<Poem> findByAuthor(@Param("author") String author);
    List<Poem> findRandom(@Param("num") int num);
    Poem findOneRandom();
    long count();
    long countByAuthor(@Param("author") String author);
    int batchInsert(@Param("list") List<Poem> poems);
    int deleteAll();
    List<Poem> findAll();
    List<Poem> findAllWithLimit(@Param("limit") int limit);
    List<Poem> findByParagraphsContaining(@Param("keyword") String keyword, @Param("limit") int limit);
    List<String> findDistinctAuthors();
    List<Poem> findByAuthorIn(@Param("authors") List<String> authors, @Param("limit") int limit);

    // For analytics: top authors
    List<java.util.Map<String, Object>> findTopAuthors(@Param("limit") int limit);
    long countDistinctAuthors();

    // Admin CRUD
    int insertOne(Poem poem);
    int update(Poem poem);
    int deleteById(@Param("id") Long id);
}
