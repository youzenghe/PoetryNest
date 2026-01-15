package com.poetry.service;

import com.poetry.dto.request.LearningTaskRequest;
import com.poetry.dto.response.LearningTaskVO;
import com.poetry.entity.LearningTask;
import com.poetry.mapper.LearningTaskMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningTaskService {

    @Autowired
    private LearningTaskMapper taskMapper;

    @Autowired
    private NotificationService notificationService;

    public LearningTaskVO createTask(Long userId, LearningTaskRequest request) {
        LearningTask task = new LearningTask();
        task.setUserId(userId);
        task.setPoemId(request.getPoemId());
        task.setTitle(request.getTitle());
        task.setStatus("PENDING");
        task.setDeadline(request.getDeadline());
        task.setReminded(false);
        taskMapper.insert(task);
        return toVO(taskMapper.findById(task.getId()));
    }

    public LearningTaskVO createTaskForUser(Long userId, Long poemId, String title, LocalDateTime deadline) {
        LearningTask task = new LearningTask();
        task.setUserId(userId);
        task.setPoemId(poemId);
        task.setTitle(title);
        task.setStatus("PENDING");
        task.setDeadline(deadline);
        task.setReminded(false);
        taskMapper.insert(task);
        return toVO(taskMapper.findById(task.getId()));
    }

    public List<LearningTaskVO> getUserTasks(Long userId) {
        return taskMapper.findByUserId(userId).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    public LearningTaskVO startTask(Long taskId, Long userId) {
        LearningTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        if (!task.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此任务");
        }
        if (!"PENDING".equals(task.getStatus())) {
            throw new IllegalArgumentException("任务状态不允许开始");
        }
        taskMapper.updateStatus(taskId, "IN_PROGRESS");
        return toVO(taskMapper.findById(taskId));
    }

    public LearningTaskVO completeTask(Long taskId, Long userId) {
        LearningTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        if (!task.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此任务");
        }
        if (!"PENDING".equals(task.getStatus()) && !"IN_PROGRESS".equals(task.getStatus())) {
            throw new IllegalArgumentException("任务状态不允许完成");
        }
        taskMapper.updateStatus(taskId, "COMPLETED");
        return toVO(taskMapper.findById(taskId));
    }

    public void cancelOverdueTasks() {
        List<LearningTask> overdue = taskMapper.findOverdueTasks(LocalDateTime.now());
        for (LearningTask task : overdue) {
            taskMapper.updateStatus(task.getId(), "CANCELLED");
            notificationService.sendNotification(task.getUserId(), "TASK_CANCELLED",
                    java.util.Map.of("taskId", task.getId(), "title", task.getTitle(), "message", "学习任务已超时取消"));
        }
    }

    public void sendReminders() {
        LocalDateTime deadline = LocalDateTime.now().plusMinutes(30);
        List<LearningTask> tasks = taskMapper.findPendingBeforeDeadline(deadline);
        for (LearningTask task : tasks) {
            notificationService.sendNotification(task.getUserId(), "TASK_REMINDER",
                    java.util.Map.of("taskId", task.getId(), "title", task.getTitle(), "deadline", task.getDeadline().toString(),
                            "message", "学习任务即将到期，请尽快完成"));
            taskMapper.updateReminded(task.getId(), true);
        }
    }

    private LearningTaskVO toVO(LearningTask task) {
        LearningTaskVO vo = new LearningTaskVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }
}
