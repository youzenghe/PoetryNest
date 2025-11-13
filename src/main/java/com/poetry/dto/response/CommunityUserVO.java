package com.poetry.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class CommunityUserVO {
    private Long userId;
    private String username;
    private String avatar;
    private String bio;
    private String location;

    // 社区统计
    private Integer articlesCount;
    private Integer totalViews;
    private Integer totalLikes;
    private Integer totalComments;

    // 关注关系
    private Integer followingCount;
    private Integer followersCount;
    private Boolean isFollowing;

    // 用户文章
    private List<ArticleVO> articles;
}
