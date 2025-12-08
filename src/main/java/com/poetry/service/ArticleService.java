package com.poetry.service;

import com.poetry.common.PageResult;
import com.poetry.dto.request.ArticleCreateRequest;
import com.poetry.dto.response.ArticleVO;
import com.poetry.entity.Article;
import com.poetry.mapper.ArticleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    public PageResult<ArticleVO> getArticles(String query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.findPublished(query);
        PageInfo<Article> pageInfo = new PageInfo<>(articles);
        List<ArticleVO> voList = pageInfo.getList().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(voList, pageInfo.getTotal(), pageNum, pageSize);
    }

    public ArticleVO getDetail(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null || !article.getIsPublished()) {
            throw new IllegalArgumentException("文章不存在");
        }
        articleMapper.incrementViews(id);
        article.setViewsCount(article.getViewsCount() + 1);
        return toVO(article);
    }

    public List<ArticleVO> getPopular() {
        return articleMapper.findPopular(5).stream().map(this::toVO).collect(Collectors.toList());
    }

    public ArticleVO createArticle(Long userId, ArticleCreateRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setAuthorId(userId);
        article.setImage(request.getImage() != null ? request.getImage() : "");
        articleMapper.insert(article);
        return toVO(articleMapper.findById(article.getId()));
    }

    public List<ArticleVO> getByAuthor(Long authorId) {
        return articleMapper.findByAuthorId(authorId).stream().map(this::toVO).collect(Collectors.toList());
    }

    public ArticleVO updateArticle(Long articleId, Long userId, ArticleCreateRequest request) {
        Article article = articleMapper.findById(articleId);
        if (article == null || !article.getIsPublished()) {
            throw new IllegalArgumentException("文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("只能编辑自己的文章");
        }
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        if (request.getImage() != null) {
            article.setImage(request.getImage());
        }
        articleMapper.updateArticle(article);
        return toVO(articleMapper.findById(articleId));
    }

    public void deleteArticle(Long articleId, Long userId) {
        Article article = articleMapper.findById(articleId);
        if (article == null) {
            throw new IllegalArgumentException("文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("只能删除自己的文章");
        }
        articleMapper.deleteById(articleId);
    }

    private ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        vo.setAuthorName(article.getAuthorName());
        return vo;
    }
}
