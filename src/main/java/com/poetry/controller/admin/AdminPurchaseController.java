package com.poetry.controller.admin;

import com.poetry.common.PageResult;
import com.poetry.common.Result;
import com.poetry.dto.request.AdminPurchaseRequest;
import com.poetry.dto.response.UserPurchaseVO;
import com.poetry.service.UserPurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchases")
public class AdminPurchaseController {

    @Autowired
    private UserPurchaseService purchaseService;

    @GetMapping
    public Result<PageResult<UserPurchaseVO>> list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return Result.success(purchaseService.getAllPurchases(page, size));
    }

    @GetMapping("/user/{userId}")
    public Result<List<UserPurchaseVO>> getByUser(@PathVariable Long userId) {
        return Result.success(purchaseService.getByUserId(userId));
    }

    @PostMapping
    public Result<UserPurchaseVO> create(@Valid @RequestBody AdminPurchaseRequest request) {
        UserPurchaseVO vo = purchaseService.adminCreatePurchase(
                request.getUserId(), request.getPoemId(), request.getAmount(), request.getExpiredAt());
        return Result.success("购买记录创建成功", vo);
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        purchaseService.cancelPurchase(id);
        return Result.success("购买记录已取消", null);
    }
}
