package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.security.UserContext;
import com.poetry.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/favorites")
public class UserFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping
    public Result<Map<String, Object>> addFavorite(@RequestBody Map<String, Long> body) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Long poemId = body.get("poemId");
        return Result.success(favoriteService.addFavorite(userId, poemId));
    }

    @DeleteMapping("/{poemId}")
    public Result<Map<String, Object>> removeFavorite(@PathVariable Long poemId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(favoriteService.removeFavorite(userId, poemId));
    }

    @GetMapping("/check")
    public Result<Map<String, Object>> checkFavorite(@RequestParam Long poemId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        boolean isFavorite = favoriteService.checkFavorite(userId, poemId);
        return Result.success(Map.of("isFavorite", isFavorite));
    }

    @GetMapping
    public Result<Map<String, Object>> getFavorites() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        List<Map<String, Object>> favorites = favoriteService.getUserFavorites(userId);
        return Result.success(Map.of("favorites", favorites, "total", favorites.size()));
    }
}
