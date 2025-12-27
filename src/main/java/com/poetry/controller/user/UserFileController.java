package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user/files")
public class UserFileController {

    @Autowired(required = false)
    private OssService ossService;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (ossService == null) {
                return Result.error("OSS未配置，无法上传文件");
            }
            String url = ossService.uploadFile(file);
            return Result.success("上传成功", Map.of("url", url));
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
