package com.poetry.mapper;

import com.poetry.entity.LearningTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LearningTaskMapper {
    LearningTask findById(@Param("id") Long id);
    List<LearningTask> findByUserId(@Param("userId") Long userId);
    List<LearningTask> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    List<LearningTask> findPendingBeforeDeadline(@Param("deadline") LocalDateTime deadline);
    List<LearningTask> findOverdueTasks(@Param("now") LocalDateTime now);
    int insert(LearningTask task);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    int updateReminded(@Param("id") Long id, @Param("reminded") boolean reminded);
}
