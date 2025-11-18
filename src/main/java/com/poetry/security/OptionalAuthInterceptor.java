package com.poetry.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 可选认证拦截器：有合法 token 就解析 UserContext，没有也放行。
 * 用于 GET /api/articles 等公开但可受益于身份信息的端点。
 */
@Component
public class OptionalAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.isTokenValid(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    Boolean hasToken = redisTemplate.hasKey("user:token:" + userId);
                    if (hasToken != null && hasToken) {
                        UserContext.setUserId(userId);
                        UserContext.setUsername(jwtUtil.getUsername(token));
                        UserContext.setRole(jwtUtil.getRole(token));
                    }
                }
            } catch (Exception ignored) {
                // Token 无效时静默忽略
            }
        }
        return true; // 始终放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
