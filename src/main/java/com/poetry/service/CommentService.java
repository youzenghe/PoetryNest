package com.poetry.service;

import com.poetry.common.PageResult;
import com.poetry.dto.response.CommentVO;
import com.poetry.entity.Article;
import com.poetry.entity.Comment;
import com.poetry.mapper.ArticleMapper;
import com.poetry.mapper.CommentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired(required = false)
    private NotificationService notificationService;

    public PageResult<CommentVO> getComments(Long articleId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> comments = commentMapper.findTopLevelByArticleId(articleId);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        List<CommentVO> voList = pageInfo.getList().stream().map(c -> {
            CommentVO vo = toVO(c);
            // Load replies
            List<Comment> replies = commentMapper.findReplies(c.getId());
            vo.setReplies(replies.stream().map(this::toVO).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, pageInfo.getTotal(), pageNum, pageSize);
    }

    public CommentVO addComment(Long articleId, Long userId, String content, Long parentId) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setAuthorId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        commentMapper.insert(comment);

        // Update article comments count
        int count = commentMapper.countActiveByArticleId(articleId);
        articleMapper.updateCommentsCount(articleId, count);

        // Send notification to article author
        if (notificationService != null) {
            Article article = articleMapper.findById(articleId);
            if (article != null && !article.getAuthorId().equals(userId)) {
                notificationService.sendNotification(article.getAuthorId(), "COMMENT",
                        Map.of("articleId", articleId,
                               "articleTitle", article.getTitle(),
                               "commentContent", content.length() > 50 ? content.substring(0, 50) + "..." : content,
                               "userId", userId));
            }

            // If it's a reply, also notify the parent comment author
            if (parentId != null) {
                List<Comment> allComments = commentMapper.findByArticleId(articleId);
                allComments.stream()
                        .filter(c -> c.getId().equals(parentId) && !c.getAuthorId().equals(userId))
                        .findFirst()
                        .ifPresent(parent -> notificationService.sendNotification(parent.getAuthorId(), "COMMENT",
                                Map.of("articleId", articleId,
                                       "commentContent", content.length() > 50 ? content.substring(0, 50) + "..." : content,
                                       "userId", userId,
                                       "isReply", true)));
            }
        }

        // Reload with author name
        List<Comment> all = commentMapper.findByArticleId(articleId);
        Comment inserted = all.stream().filter(c -> c.getId().equals(comment.getId())).findFirst().orElse(comment);
        return toVO(inserted);
    }

    private CommentVO toVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setArticleId(comment.getArticleId());
        vo.setAuthorId(comment.getAuthorId());
        vo.setAuthorName(comment.getAuthorName());
        vo.setContent(comment.getContent());
        vo.setParentId(comment.getParentId());
        vo.setIsReply(comment.getParentId() != null);
        vo.setCreatedAt(comment.getCreatedAt());
        return vo;
    }
}
