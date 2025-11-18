package com.poetry.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poetry.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String role = UserContext.getRole();
        if (role == null || !"ADMIN".equals(role)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(403);
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "需要管理员权限")));
            return false;
        }
        return true;
    }
}
