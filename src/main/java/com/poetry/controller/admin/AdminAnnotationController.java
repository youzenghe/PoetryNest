package com.poetry.controller.admin;

import com.poetry.common.Result;
import com.poetry.dto.request.AnnotationRequest;
import com.poetry.dto.response.PoemAnnotationVO;
import com.poetry.security.UserContext;
import com.poetry.service.PoemAnnotationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/annotations")
public class AdminAnnotationController {

    @Autowired
    private PoemAnnotationService annotationService;

    @GetMapping
    public Result<List<PoemAnnotationVO>> getByPoem(@RequestParam Long poemId) {
        return Result.success(annotationService.getByPoemId(poemId));
    }

    @GetMapping("/{id}")
    public Result<PoemAnnotationVO> getById(@PathVariable Long id) {
        return Result.success(annotationService.getById(id));
    }

    @PostMapping
    public Result<PoemAnnotationVO> create(@Valid @RequestBody AnnotationRequest request) {
        Long adminId = UserContext.getUserId();
        return Result.success("注解创建成功", annotationService.create(request, adminId));
    }

    @PutMapping("/{id}")
    public Result<PoemAnnotationVO> update(@PathVariable Long id, @Valid @RequestBody AnnotationRequest request) {
        Long adminId = UserContext.getUserId();
        return Result.success("注解更新成功", annotationService.update(id, request, adminId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        annotationService.delete(id);
        return Result.success("注解删除成功", null);
    }
}
