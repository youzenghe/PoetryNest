package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.dto.request.ProfileUpdateRequest;
import com.poetry.dto.response.UserProfileVO;
import com.poetry.security.UserContext;
import com.poetry.service.OssService;
import com.poetry.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired(required = false)
    private OssService ossService;

    @GetMapping
    public Result<UserProfileVO> getMyProfile() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(profileService.getProfile(userId));
    }

    @PutMapping
    public Result<UserProfileVO> updateProfile(@RequestBody ProfileUpdateRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success("资料更新成功", profileService.updateProfile(userId, request));
    }

    @GetMapping("/{userId}")
    public Result<UserProfileVO> getUserProfile(@PathVariable Long userId) {
        return Result.success(profileService.getProfile(userId));
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        try {
            if (ossService == null) {
                return Result.error("OSS未配置，无法上传头像");
            }
            String url = ossService.uploadFile(file);
            profileService.updateAvatar(userId, url);
            return Result.success("头像上传成功", Map.of("avatar", url));
        } catch (Exception e) {
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }
}
