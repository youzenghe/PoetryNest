package com.poetry.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // userId -> WebSocketSession
    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Notification WebSocket connected: {}", session.getId());
        // Client should send userId after connection
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            if ("register".equals(data.get("type"))) {
                Long userId = Long.valueOf(data.get("userId").toString());
                sessions.put(userId, session);
                log.info("User {} registered for notifications", userId);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Map.of("type", "registered", "message", "通知注册成功"))));
            }
        } catch (Exception e) {
            log.error("Failed to handle notification message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().removeIf(s -> s.getId().equals(session.getId()));
        log.info("Notification WebSocket disconnected: {}", session.getId());
    }

    public void sendToUser(Long userId, Map<String, Object> data) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(data)));
            } catch (IOException e) {
                log.error("Failed to send notification to user {}", userId, e);
            }
        }
    }

    public boolean isUserOnline(Long userId) {
        WebSocketSession session = sessions.get(userId);
        return session != null && session.isOpen();
    }
}
