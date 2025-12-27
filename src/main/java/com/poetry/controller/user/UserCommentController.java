package com.poetry.controller.user;

import com.poetry.common.PageResult;
import com.poetry.common.Result;
import com.poetry.dto.request.CommentCreateRequest;
import com.poetry.dto.response.CommentVO;
import com.poetry.security.UserContext;
import com.poetry.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/articles/{articleId}/comments")
public class UserCommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result<PageResult<CommentVO>> list(
            @PathVariable Long articleId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return Result.success(commentService.getComments(articleId, page, size));
    }

    @PostMapping
    public Result<CommentVO> create(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentCreateRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        CommentVO vo = commentService.addComment(articleId, userId,
                request.getContent(), request.getParentId());
        return Result.success("评论发布成功", vo);
    }
}
