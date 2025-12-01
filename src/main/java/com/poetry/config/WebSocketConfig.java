package com.poetry.config;

import com.poetry.websocket.AiChatWebSocketHandler;
import com.poetry.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private AiChatWebSocketHandler aiChatHandler;

    @Autowired
    private NotificationWebSocketHandler notificationHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(aiChatHandler, "/ws/ai-chat")
                .setAllowedOrigins("*");
        registry.addHandler(notificationHandler, "/ws/notification")
                .setAllowedOrigins("*");
    }
}
