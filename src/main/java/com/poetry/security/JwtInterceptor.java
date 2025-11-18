package com.poetry.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poetry.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "未登录或登录已过期");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            sendError(response, 401, "Token无效或已过期");
            return false;
        }

        Long userId = jwtUtil.getUserId(token);

        // 校验 Redis 白名单：登出后 key 已删除，此处应拒绝
        Boolean hasToken = redisTemplate.hasKey("user:token:" + userId);
        if (hasToken == null || !hasToken) {
            sendError(response, 401, "登录已失效，请重新登录");
            return false;
        }

        UserContext.setUserId(userId);
        UserContext.setUsername(jwtUtil.getUsername(token));
        UserContext.setRole(jwtUtil.getRole(token));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, message)));
    }
}
