package com.poetry.controller.admin;

import com.poetry.common.PageResult;
import com.poetry.common.Result;
import com.poetry.dto.response.AdminUserVO;
import com.poetry.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public Result<PageResult<AdminUserVO>> list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return Result.success(adminUserService.getAllUsers(page, size));
    }

    @GetMapping("/{id}")
    public Result<AdminUserVO> detail(@PathVariable Long id) {
        return Result.success(adminUserService.getUserDetail(id));
    }

    @PutMapping("/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        if (role == null || (!role.equals("USER") && !role.equals("ADMIN"))) {
            return Result.error(400, "角色必须为 USER 或 ADMIN");
        }
        adminUserService.updateUserRole(id, role);
        return Result.success("角色更新成功", null);
    }
}
