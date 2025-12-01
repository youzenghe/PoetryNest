package com.poetry.config;

import com.poetry.security.AdminInterceptor;
import com.poetry.security.JwtInterceptor;
import com.poetry.security.OptionalAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private OptionalAuthInterceptor optionalAuthInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 可选认证拦截器（有 token 就解析，没有也放行）
        registry.addInterceptor(optionalAuthInterceptor)
                .addPathPatterns(
                        "/api/user/articles",
                        "/api/user/articles/*/comments"
                )
                .order(0);

        // 2. 强制认证拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 认证
                        "/api/user/auth/login",
                        "/api/user/auth/register",
                        // 诗词（全部公开）
                        "/api/user/poems/**",
                        // 答题（公开）
                        "/api/user/quiz/**",
                        // 文章列表（GET 公开）
                        "/api/user/articles",
                        "/api/user/articles/popular",
                        "/api/user/articles/*/comments",
                        "/api/user/articles/*",
                        // 社区用户主页（公开）
                        "/api/user/community/user/**",
                        "/api/user/community/users/*/stats"
                )
                .order(1);

        // 3. Admin 角色校验
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .order(2);
    }
}
