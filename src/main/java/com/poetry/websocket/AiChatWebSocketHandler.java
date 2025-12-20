package com.poetry.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poetry.service.DeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AiChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(AiChatWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    private DeepSeekService deepSeekService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = UUID.randomUUID().toString();
        sessionMap.put(session.getId(), sessionId);
        log.info("AI Chat WebSocket connected: {}, sessionId: {}", session.getId(), sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("AI Chat received: {}", payload);

        String sessionId = sessionMap.getOrDefault(session.getId(), "default");

        if (!deepSeekService.isConfigured()) {
            Map<String, Object> response = Map.of(
                    "type", "ai_response",
                    "content", "AI服务未配置，请在application-dev.yml中设置deepseek.api-key",
                    "done", true
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        // Parse the incoming message
        String userMessage;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            userMessage = (String) data.getOrDefault("message", payload);
        } catch (Exception e) {
            userMessage = payload;
        }

        // Call DeepSeek API
        String reply = deepSeekService.chat(userMessage, sessionId);

        Map<String, Object> response = Map.of(
                "type", "ai_response",
                "content", reply,
                "done", true
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.remove(session.getId());
        log.info("AI Chat WebSocket disconnected: {}", session.getId());
    }
}
