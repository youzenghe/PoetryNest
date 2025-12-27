package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.dto.request.PurchaseRequest;
import com.poetry.dto.response.UserPurchaseVO;
import com.poetry.security.UserContext;
import com.poetry.service.UserPurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/purchases")
public class UserPurchaseController {

    @Autowired
    private UserPurchaseService purchaseService;

    @PostMapping
    public Result<UserPurchaseVO> purchase(@Valid @RequestBody PurchaseRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        UserPurchaseVO vo = purchaseService.mockWechatPay(userId, request.getPoemId());
        return Result.success("支付成功", vo);
    }

    @GetMapping
    public Result<List<UserPurchaseVO>> myPurchases() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(purchaseService.getUserPurchases(userId));
    }

    @GetMapping("/check")
    public Result<Map<String, Object>> checkPurchase(@RequestParam Long poemId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        boolean purchased = purchaseService.checkPurchase(userId, poemId);
        return Result.success(Map.of("purchased", purchased));
    }
}
