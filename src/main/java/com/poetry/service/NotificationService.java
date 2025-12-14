package com.poetry.service;

import com.poetry.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationWebSocketHandler notificationHandler;

    /**
     * Send real-time notification to a user
     * @param userId target user
     * @param type COMMENT, LIKE, FOLLOW
     * @param data notification payload
     */
    public void sendNotification(Long userId, String type, Map<String, Object> data) {
        Map<String, Object> notification = new LinkedHashMap<>();
        notification.put("type", "notification");
        notification.put("notificationType", type);
        notification.put("data", data);
        notification.put("timestamp", LocalDateTime.now().toString());

        notificationHandler.sendToUser(userId, notification);
    }
}
