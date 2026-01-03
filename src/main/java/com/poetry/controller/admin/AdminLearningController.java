package com.poetry.controller.admin;

import com.poetry.common.Result;
import com.poetry.dto.request.AdminLearningTaskRequest;
import com.poetry.dto.response.LearningTaskVO;
import com.poetry.service.LearningTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tasks")
public class AdminLearningController {

    @Autowired
    private LearningTaskService taskService;

    @PostMapping
    public Result<LearningTaskVO> assignTask(@Valid @RequestBody AdminLearningTaskRequest request) {
        LearningTaskVO vo = taskService.createTaskForUser(
                request.getUserId(), request.getPoemId(), request.getTitle(), request.getDeadline());
        return Result.success("学习任务分配成功", vo);
    }

    @GetMapping("/user/{userId}")
    public Result<List<LearningTaskVO>> getByUser(@PathVariable Long userId) {
        return Result.success(taskService.getUserTasks(userId));
    }
}
