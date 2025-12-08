package com.poetry.service;

import com.poetry.common.ErrorCode;
import com.poetry.entity.User;
import com.poetry.entity.UserProfile;
import com.poetry.mapper.UserMapper;
import com.poetry.mapper.UserProfileMapper;
import com.poetry.security.JwtUtil;
import com.poetry.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper profileMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> register(String username, String password) {
        if (userMapper.countByUsername(username) > 0) {
            throw new IllegalArgumentException(ErrorCode.USERNAME_EXISTS.getMessage());
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password));
        userMapper.insert(user);

        // 自动创建用户资料
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setAvatar("");
        profile.setBio("");
        profile.setLocation("");
        profile.setPreferredDynasty("");
        profile.setFavoriteStyle("");
        profile.setPoemsReadCount(0);
        profile.setLoginDays(1);
        profileMapper.insert(profile);

        String role = user.getRole() != null ? user.getRole() : "USER";
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);
        redisTemplate.opsForValue().set("user:token:" + user.getId(), token, 24, TimeUnit.HOURS);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", role);
        return result;
    }

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(ErrorCode.PASSWORD_ERROR.getMessage());
        }

        String role = user.getRole() != null ? user.getRole() : "USER";
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);
        redisTemplate.opsForValue().set("user:token:" + user.getId(), token, 24, TimeUnit.HOURS);

        // 登录天数+1（每天只计一次）
        trackLoginDay(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", role);
        return result;
    }

    public void logout(Long userId) {
        redisTemplate.delete("user:token:" + userId);
    }

    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 记录登录天数，每日只累加一次
     */
    private void trackLoginDay(Long userId) {
        String todayKey = "user:login_day:" + userId + ":" + LocalDate.now();
        Boolean alreadyTracked = redisTemplate.hasKey(todayKey);
        if (alreadyTracked == null || !alreadyTracked) {
            profileMapper.incrementLoginDays(userId);
            redisTemplate.opsForValue().set(todayKey, true, 25, TimeUnit.HOURS);
        }
    }
}
