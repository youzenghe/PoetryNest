package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.dto.response.ArticleVO;
import com.poetry.dto.response.CommunityUserVO;
import com.poetry.entity.User;
import com.poetry.entity.UserProfile;
import com.poetry.mapper.UserMapper;
import com.poetry.mapper.UserProfileMapper;
import com.poetry.security.UserContext;
import com.poetry.service.ArticleService;
import com.poetry.service.FollowService;
import com.poetry.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/community")
public class UserCommunityController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper profileMapper;

    @PostMapping("/articles/{id}/like")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(likeService.toggleLike(id, userId));
    }

    @GetMapping("/articles/{id}/like-status")
    public Result<Map<String, Object>> likeStatus(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(likeService.getLikeStatus(id, userId));
    }

    @PostMapping("/users/{id}/follow")
    public Result<Map<String, Object>> toggleFollow(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(followService.toggleFollow(userId, id));
    }

    @GetMapping("/users/{id}/stats")
    public Result<Map<String, Object>> userStats(@PathVariable Long id) {
        Map<String, Object> stats = followService.getUserStats(id);
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null && !currentUserId.equals(id)) {
            stats.put("isFollowing", followService.isFollowing(currentUserId, id));
        } else {
            stats.put("isFollowing", false);
        }
        return Result.success(stats);
    }

    @GetMapping("/user/{userId}")
    public Result<CommunityUserVO> communityUserProfile(@PathVariable Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        UserProfile profile = profileMapper.findByUserId(userId);

        CommunityUserVO vo = new CommunityUserVO();
        vo.setUserId(userId);
        vo.setUsername(user.getUsername());
        vo.setAvatar(profile != null ? profile.getAvatar() : "");
        vo.setBio(profile != null ? profile.getBio() : "");
        vo.setLocation(profile != null ? profile.getLocation() : "");

        List<ArticleVO> articles = articleService.getByAuthor(userId);
        vo.setArticles(articles);

        vo.setArticlesCount(articles.size());
        int totalViews = 0, totalLikes = 0, totalComments = 0;
        for (ArticleVO a : articles) {
            totalViews += a.getViewsCount() != null ? a.getViewsCount() : 0;
            totalLikes += a.getLikesCount() != null ? a.getLikesCount() : 0;
            totalComments += a.getCommentsCount() != null ? a.getCommentsCount() : 0;
        }
        vo.setTotalViews(totalViews);
        vo.setTotalLikes(totalLikes);
        vo.setTotalComments(totalComments);

        Map<String, Object> followStats = followService.getUserStats(userId);
        vo.setFollowingCount(((Number) followStats.get("followingCount")).intValue());
        vo.setFollowersCount(((Number) followStats.get("followersCount")).intValue());

        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null && !currentUserId.equals(userId)) {
            vo.setIsFollowing(followService.isFollowing(currentUserId, userId));
        } else {
            vo.setIsFollowing(false);
        }

        return Result.success(vo);
    }
}
