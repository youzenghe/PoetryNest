package com.poetry.service;

import com.poetry.entity.Article;
import com.poetry.entity.ArticleLike;
import com.poetry.mapper.ArticleLikeMapper;
import com.poetry.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LikeService {

    @Autowired
    private ArticleLikeMapper likeMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired(required = false)
    private NotificationService notificationService;

    public Map<String, Object> toggleLike(Long articleId, Long userId) {
        Article article = articleMapper.findById(articleId);
        if (article == null) {
            throw new IllegalArgumentException("文章不存在");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        ArticleLike existing = likeMapper.findByArticleAndUser(articleId, userId);

        if (existing == null) {
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            likeMapper.insert(like);
            int count = likeMapper.countByArticleId(articleId);
            articleMapper.updateLikesCount(articleId, count);
            result.put("action", "liked");
            result.put("message", "点赞成功");
            result.put("likesCount", count);

            // Send notification
            if (notificationService != null && !article.getAuthorId().equals(userId)) {
                notificationService.sendNotification(article.getAuthorId(), "LIKE",
                        Map.of("articleId", articleId, "articleTitle", article.getTitle(), "userId", userId));
            }
        } else {
            likeMapper.deleteByArticleAndUser(articleId, userId);
            int count = likeMapper.countByArticleId(articleId);
            articleMapper.updateLikesCount(articleId, count);
            result.put("action", "unliked");
            result.put("message", "取消点赞");
            result.put("likesCount", count);
        }

        return result;
    }

    public Map<String, Object> getLikeStatus(Long articleId, Long userId) {
        ArticleLike existing = likeMapper.findByArticleAndUser(articleId, userId);
        Article article = articleMapper.findById(articleId);
        int count = article != null ? article.getLikesCount() : 0;
        return Map.of("isLiked", existing != null, "likesCount", count);
    }
}
