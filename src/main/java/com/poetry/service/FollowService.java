package com.poetry.service;

import com.poetry.entity.UserFollow;
import com.poetry.mapper.UserFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FollowService {

    @Autowired
    private UserFollowMapper followMapper;

    @Autowired(required = false)
    private NotificationService notificationService;

    public Map<String, Object> toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("不能关注自己");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        UserFollow existing = followMapper.findByFollowerAndFollowing(followerId, followingId);

        if (existing == null) {
            UserFollow follow = new UserFollow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            followMapper.insert(follow);
            result.put("action", "followed");
            result.put("message", "关注成功");

            // Send notification
            if (notificationService != null) {
                notificationService.sendNotification(followingId, "FOLLOW",
                        Map.of("followerId", followerId));
            }
        } else {
            followMapper.deleteByFollowerAndFollowing(followerId, followingId);
            result.put("action", "unfollowed");
            result.put("message", "已取消关注");
        }

        result.put("followingCount", followMapper.countFollowing(followingId));
        result.put("followersCount", followMapper.countFollowers(followingId));
        return result;
    }

    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("followingCount", followMapper.countFollowing(userId));
        stats.put("followersCount", followMapper.countFollowers(userId));
        return stats;
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return followMapper.findByFollowerAndFollowing(followerId, followingId) != null;
    }
}
