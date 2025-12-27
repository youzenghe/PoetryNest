package com.poetry.controller.user;

import com.poetry.common.PageResult;
import com.poetry.common.Result;
import com.poetry.dto.request.ArticleCreateRequest;
import com.poetry.dto.response.ArticleVO;
import com.poetry.security.UserContext;
import com.poetry.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/articles")
public class UserArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public Result<PageResult<ArticleVO>> list(
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return Result.success(articleService.getArticles(query, page, size));
    }

    @PostMapping
    public Result<ArticleVO> create(@Valid @RequestBody ArticleCreateRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success("文章发布成功", articleService.createArticle(userId, request));
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getDetail(id));
    }

    @GetMapping("/popular")
    public Result<List<ArticleVO>> popular() {
        return Result.success(articleService.getPopular());
    }

    @PutMapping("/{id}")
    public Result<ArticleVO> update(@PathVariable Long id,
                                     @Valid @RequestBody ArticleCreateRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success("文章更新成功", articleService.updateArticle(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        articleService.deleteArticle(id, userId);
        return Result.success("文章删除成功", null);
    }
}
