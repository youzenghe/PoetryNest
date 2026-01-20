package com.poetry.schedule;

import com.poetry.service.LearningTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class LearningTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(LearningTaskScheduler.class);

    @Autowired
    private LearningTaskService taskService;

    @Scheduled(fixedDelay = 60000)
    public void checkOverdueTasks() {
        try {
            taskService.cancelOverdueTasks();
        } catch (Exception e) {
            log.error("Failed to check overdue tasks", e);
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void sendReminders() {
        try {
            taskService.sendReminders();
        } catch (Exception e) {
            log.error("Failed to send task reminders", e);
        }
    }
}
