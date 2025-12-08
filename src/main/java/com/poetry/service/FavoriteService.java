package com.poetry.service;

import com.poetry.entity.Poem;
import com.poetry.entity.UserFavorite;
import com.poetry.mapper.PoemMapper;
import com.poetry.mapper.UserFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private UserFavoriteMapper favoriteMapper;

    @Autowired
    private PoemMapper poemMapper;

    public Map<String, Object> addFavorite(Long userId, Long poemId) {
        Poem poem = poemMapper.findById(poemId);
        if (poem == null) {
            throw new IllegalArgumentException("诗词不存在");
        }

        int exists = favoriteMapper.countByUserAndPoem(userId, poemId);
        if (exists > 0) {
            return Map.of("action", "exists", "message", "已经收藏过了");
        }

        UserFavorite fav = new UserFavorite();
        fav.setUserId(userId);
        fav.setPoemId(poemId);
        favoriteMapper.insert(fav);
        return Map.of("action", "added", "message", "收藏成功");
    }

    public Map<String, Object> removeFavorite(Long userId, Long poemId) {
        int exists = favoriteMapper.countByUserAndPoem(userId, poemId);
        if (exists == 0) {
            return Map.of("action", "not_exists", "message", "未收藏此诗词");
        }
        favoriteMapper.deleteByUserAndPoem(userId, poemId);
        return Map.of("action", "removed", "message", "取消收藏成功");
    }

    public boolean checkFavorite(Long userId, Long poemId) {
        return favoriteMapper.countByUserAndPoem(userId, poemId) > 0;
    }

    public List<Map<String, Object>> getUserFavorites(Long userId) {
        List<UserFavorite> favorites = favoriteMapper.findByUserId(userId);
        return favorites.stream().map(f -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", f.getPoemId());
            map.put("title", f.getPoemTitle());
            map.put("author", f.getPoemAuthor());
            map.put("paragraphs", f.getPoemParagraphs());
            map.put("favoriteTime", f.getFavoriteTime());
            return map;
        }).collect(Collectors.toList());
    }
}
