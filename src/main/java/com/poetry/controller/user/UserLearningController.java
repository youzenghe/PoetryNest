package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.dto.request.LearningTaskRequest;
import com.poetry.dto.response.LearningTaskVO;
import com.poetry.security.UserContext;
import com.poetry.service.LearningTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/tasks")
public class UserLearningController {

    @Autowired
    private LearningTaskService taskService;

    @PostMapping
    public Result<LearningTaskVO> createTask(@Valid @RequestBody LearningTaskRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success("任务创建成功", taskService.createTask(userId, request));
    }

    @GetMapping
    public Result<List<LearningTaskVO>> myTasks() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(taskService.getUserTasks(userId));
    }

    @PutMapping("/{id}/start")
    public Result<LearningTaskVO> startTask(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success("任务已开始", taskService.startTask(id, userId));
    }

    @PutMapping("/{id}/complete")
    public Result<LearningTaskVO> completeTask(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success("任务已完成", taskService.completeTask(id, userId));
    }
}
