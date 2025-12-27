package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.dto.request.LoginRequest;
import com.poetry.dto.request.RegisterRequest;
import com.poetry.security.UserContext;
import com.poetry.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/auth")
public class UserAuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> data = userService.register(request.getUsername(), request.getPassword());
        return Result.success("注册成功", data);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", data);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            userService.logout(userId);
        }
        return Result.success("已退出登录", null);
    }
}
